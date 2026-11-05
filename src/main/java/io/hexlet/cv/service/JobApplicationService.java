package io.hexlet.cv.service;

import io.hexlet.cv.dto.application.ApplicationDTOs.Alert;
import io.hexlet.cv.dto.application.ApplicationDTOs.Details;
import io.hexlet.cv.dto.application.ApplicationDTOs.Event;
import io.hexlet.cv.dto.application.ApplicationDTOs.ListResponse;
import io.hexlet.cv.dto.application.ApplicationDTOs.Rejections;
import io.hexlet.cv.dto.application.ApplicationDTOs.ResumeRef;
import io.hexlet.cv.dto.application.ApplicationDTOs.Summary;
import io.hexlet.cv.dto.application.ApplicationFormDTO;
import io.hexlet.cv.dto.application.EventFormDTO;
import io.hexlet.cv.dto.application.StageChangeDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.model.JobApplication;
import io.hexlet.cv.model.JobApplicationEvent;
import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.ApplicationEventType;
import io.hexlet.cv.model.enums.ApplicationStage;
import io.hexlet.cv.repository.JobApplicationRepository;
import io.hexlet.cv.repository.ResumeRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Трекер откликов (#1155): отклики пользователя, стадии, таймлайн и подсказки.
 *
 * <p>Откликов у одного человека десятки, поэтому фильтры и счётчики считаются в памяти
 * по всем откликам пользователя — так счётчики на чипах всегда согласованы со списком.
 */
@Service
public class JobApplicationService {

    /** Дней без событий, после которых предлагаем фоллоу-ап. */
    static final long SILENCE_DAYS = 7;
    /** За сколько дней предупреждаем о дедлайне. */
    static final long DEADLINE_DAYS = 3;

    private static final Set<ApplicationStage> WAITING =
            Set.of(ApplicationStage.APPLIED, ApplicationStage.SCREENING, ApplicationStage.INTERVIEW);
    private static final Set<ApplicationEventType> MANUAL_EVENTS = Set.of(
            ApplicationEventType.REPLY, ApplicationEventType.INTERVIEW,
            ApplicationEventType.LETTER, ApplicationEventType.NOTE);

    private static final Map<ApplicationStage, String> LABELS = Map.of(
            ApplicationStage.SAVED, "Сохранено",
            ApplicationStage.APPLIED, "Отклик отправлен",
            ApplicationStage.SCREENING, "Скрининг",
            ApplicationStage.INTERVIEW, "Собеседование",
            ApplicationStage.OFFER, "Оффер",
            ApplicationStage.REJECTED, "Отказ");

    private final JobApplicationRepository applications;
    private final ResumeRepository resumes;
    private final Clock clock;

    @Autowired
    public JobApplicationService(JobApplicationRepository applications, ResumeRepository resumes) {
        this(applications, resumes, Clock.systemDefaultZone());
    }

    JobApplicationService(JobApplicationRepository applications, ResumeRepository resumes, Clock clock) {
        this.applications = applications;
        this.resumes = resumes;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ListResponse list(User user, ApplicationStage stage, Long resumeId, String query) {
        var q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        var matching = applications.findByUserIdOrderByLastEventAtDesc(user.getId()).stream()
                .filter(a -> resumeId == null || a.getResume() != null && resumeId.equals(a.getResume().getId()))
                .filter(a -> q.isEmpty()
                        || a.getCompany().toLowerCase(Locale.ROOT).contains(q)
                        || a.getPosition().toLowerCase(Locale.ROOT).contains(q))
                .toList();

        Map<String, Long> counts = new LinkedHashMap<>();
        // «Все» — воронка без отказов: отказы показываются отдельной строкой под доской.
        counts.put("ALL", matching.stream().filter(a -> a.getStage() != ApplicationStage.REJECTED).count());
        for (var s : ApplicationStage.values()) {
            counts.put(s.name(), matching.stream().filter(a -> a.getStage() == s).count());
        }

        Map<ApplicationStage, Long> byStage = new EnumMap<>(ApplicationStage.class);
        matching.stream()
                .filter(a -> a.getStage() == ApplicationStage.REJECTED && a.getRejectedAtStage() != null)
                .forEach(a -> byStage.merge(a.getRejectedAtStage(), 1L, Long::sum));
        var rejections = new Rejections(counts.get(ApplicationStage.REJECTED.name()), byStage);

        var items = matching.stream()
                .filter(a -> stage == null
                        ? a.getStage() != ApplicationStage.REJECTED
                        : a.getStage() == stage)
                .map(this::toSummary)
                .toList();
        return new ListResponse(items, counts, rejections);
    }

    @Transactional(readOnly = true)
    public Details get(User user, Long id) {
        return toDetails(find(user, id));
    }

    @Transactional
    public Details create(User user, ApplicationFormDTO form) {
        var stage = form.getStage() == null ? ApplicationStage.SAVED : form.getStage();
        if (stage == ApplicationStage.REJECTED) {
            throw new ClientException("stage", "Новый отклик не может начинаться с отказа", HttpStatus.BAD_REQUEST);
        }
        var now = now();
        var a = new JobApplication();
        a.setUser(user);
        a.setStage(stage);
        a.setLastEventAt(now);
        apply(user, a, form);

        var text = stage == ApplicationStage.SAVED
                ? "Вакансия сохранена в трекер"
                : "Добавлен на этапе «" + LABELS.get(stage) + "»";
        if (a.getSource() != null && !a.getSource().isBlank()) {
            text += " · источник: " + a.getSource();
        }
        addEvent(a, ApplicationEventType.CREATED, text, now);
        return toDetails(applications.save(a));
    }

    @Transactional
    public Details update(User user, Long id, ApplicationFormDTO form) {
        var a = find(user, id);
        apply(user, a, form);
        return toDetails(a);
    }

    @Transactional
    public Details changeStage(User user, Long id, StageChangeDTO dto) {
        var a = find(user, id);
        var previous = a.getStage();
        if (previous == dto.getStage()) {
            return toDetails(a);
        }
        var now = now();
        a.setStage(dto.getStage());
        if (dto.getStage() == ApplicationStage.REJECTED) {
            var at = dto.getRejectedAtStage() != null ? dto.getRejectedAtStage() : previous;
            a.setRejectedAtStage(at);
            addEvent(a, ApplicationEventType.STAGE_CHANGED, "Отказ после этапа «" + LABELS.get(at) + "»", now);
        } else {
            a.setRejectedAtStage(null);
            addEvent(a, ApplicationEventType.STAGE_CHANGED, "Этап: " + LABELS.get(dto.getStage()), now);
        }
        a.setLastEventAt(now);
        return toDetails(a);
    }

    @Transactional
    public Details addEvent(User user, Long id, EventFormDTO dto) {
        if (!MANUAL_EVENTS.contains(dto.getType())) {
            throw new ClientException("type", "Такое событие добавляется автоматически", HttpStatus.BAD_REQUEST);
        }
        var a = find(user, id);
        var at = dto.getOccurredAt() != null ? dto.getOccurredAt() : now();
        addEvent(a, dto.getType(), dto.getText().trim(), at);
        if (at.isAfter(a.getLastEventAt()) && !at.isAfter(now())) {
            a.setLastEventAt(at);
        }
        return toDetails(a);
    }

    @Transactional
    public void delete(User user, Long id) {
        applications.delete(find(user, id));
    }

    private JobApplication find(User user, Long id) {
        return applications.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Отклик не найден"));
    }

    private void apply(User user, JobApplication a, ApplicationFormDTO form) {
        a.setCompany(form.getCompany().trim());
        a.setPosition(form.getPosition().trim());
        a.setSalary(blankToNull(form.getSalary()));
        a.setVacancyUrl(blankToNull(form.getVacancyUrl()));
        a.setSource(blankToNull(form.getSource()));
        a.setDeadline(form.getDeadline());
        a.setNextStepTitle(blankToNull(form.getNextStepTitle()));
        a.setNextStepAt(form.getNextStepAt());
        a.setResume(form.getResumeId() == null ? null : ownResume(user, form.getResumeId()));
    }

    private Resume ownResume(User user, Long resumeId) {
        return resumes.findByIdAndUserId(resumeId, user.getId())
                .orElseThrow(() -> new ClientException("resumeId", "Резюме не найдено", HttpStatus.BAD_REQUEST));
    }

    private static void addEvent(JobApplication a, ApplicationEventType type, String text, LocalDateTime at) {
        var e = new JobApplicationEvent();
        e.setApplication(a);
        e.setType(type);
        e.setText(text);
        e.setOccurredAt(at);
        a.getEvents().add(e);
    }

    List<Alert> alerts(JobApplication a) {
        var result = new ArrayList<Alert>();
        var now = now();
        var hasUpcomingStep = a.getNextStepAt() != null && a.getNextStepAt().isAfter(now);
        if (WAITING.contains(a.getStage()) && !hasUpcomingStep) {
            var silent = ChronoUnit.DAYS.between(a.getLastEventAt(), now);
            if (silent >= SILENCE_DAYS) {
                result.add(new Alert("SILENCE", silent));
            }
        }
        if (a.getDeadline() != null && a.getStage() != ApplicationStage.REJECTED) {
            var left = ChronoUnit.DAYS.between(LocalDate.now(clock), a.getDeadline());
            if (left >= 0 && left <= DEADLINE_DAYS) {
                result.add(new Alert("DEADLINE", left));
            }
        }
        return result;
    }

    private Summary toSummary(JobApplication a) {
        return new Summary(a.getId(), a.getCompany(), a.getPosition(), a.getSalary(), a.getSource(), a.getStage(),
                a.getRejectedAtStage(), resumeRef(a), a.getDeadline(), a.getNextStepTitle(), a.getNextStepAt(),
                a.getLastEventAt(), a.getCreatedAt(), alerts(a));
    }

    private Details toDetails(JobApplication a) {
        var events = a.getEvents().stream()
                .map(e -> new Event(e.getId(), e.getType(), e.getText(), e.getOccurredAt()))
                .toList();
        return new Details(a.getId(), a.getCompany(), a.getPosition(), a.getSalary(), a.getVacancyUrl(),
                a.getSource(), a.getStage(), a.getRejectedAtStage(), resumeRef(a), a.getDeadline(),
                a.getNextStepTitle(), a.getNextStepAt(), a.getLastEventAt(), a.getCreatedAt(), alerts(a), events);
    }

    private static ResumeRef resumeRef(JobApplication a) {
        return a.getResume() == null ? null : new ResumeRef(a.getResume().getId(), a.getResume().getName());
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
