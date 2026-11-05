package io.hexlet.cv.service;

import io.hexlet.cv.dto.api.PageDTO;
import io.hexlet.cv.dto.resume.AuthorDTO;
import io.hexlet.cv.dto.resume.OwnResumeDTO;
import io.hexlet.cv.dto.resume.PublicProfileDTO;
import io.hexlet.cv.dto.resume.ResumeDetailsDTO;
import io.hexlet.cv.dto.resume.ResumeEducationDTO;
import io.hexlet.cv.dto.resume.ResumeEducationFormDTO;
import io.hexlet.cv.dto.resume.ResumeFormDTO;
import io.hexlet.cv.dto.resume.ResumeSummaryDTO;
import io.hexlet.cv.dto.resume.ResumeWorkDTO;
import io.hexlet.cv.dto.resume.ResumeWorkFormDTO;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.ResumeEducation;
import io.hexlet.cv.model.ResumeWork;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Резюме для REST API: публичный каталог, страница резюме, профиль автора
 * и редактирование своих резюме в кабинете.
 */
@Service
@AllArgsConstructor
public class ResumeApiService {

    /** Статус резюме, видимого в каталоге. Совпадает со значением в старом приложении. */
    public static final String PUBLISHED = "published";
    public static final String DRAFT = "draft";

    public static final int PAGE_SIZE = 20;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageDTO<ResumeSummaryDTO> search(String query, int page) {
        var q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        var pageable = PageRequest.of(Math.max(page, 1) - 1, PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "updatedAt"));
        return PageDTO.of(resumeRepository.search(PUBLISHED, q, pageable), ResumeApiService::toSummary);
    }

    @Transactional(readOnly = true)
    public ResumeDetailsDTO getPublished(Long id) {
        var resume = resumeRepository.findByIdAndState(id, PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Резюме не найдено"));
        return toDetails(resume);
    }

    @Transactional(readOnly = true)
    public PublicProfileDTO getProfile(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        var resumes = resumeRepository.findByUserIdAndStateOrderByUpdatedAtDesc(userId, PUBLISHED).stream()
                .map(ResumeApiService::toSummary)
                .toList();
        return new PublicProfileDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getAbout(), resumes);
    }

    @Transactional(readOnly = true)
    public List<OwnResumeDTO> listOwn(User user) {
        return resumeRepository.findByUserIdOrderByUpdatedAtDesc(user.getId()).stream()
                .map(ResumeApiService::toOwn)
                .toList();
    }

    @Transactional(readOnly = true)
    public OwnResumeDTO getOwn(User user, Long id) {
        return toOwn(findOwn(user, id));
    }

    @Transactional
    public OwnResumeDTO create(User user, ResumeFormDTO form) {
        var resume = new Resume();
        resume.setUser(user);
        resume.setAnswersCount(0);
        resume.setLocale("ru");
        apply(resume, form);
        return toOwn(resumeRepository.save(resume));
    }

    @Transactional
    public OwnResumeDTO update(User user, Long id, ResumeFormDTO form) {
        var resume = findOwn(user, id);
        apply(resume, form);
        return toOwn(resumeRepository.save(resume));
    }

    private Resume findOwn(User user, Long id) {
        // Чужое резюме отдаём как несуществующее, чтобы не раскрывать, что такой id есть.
        return resumeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Резюме не найдено"));
    }

    private static void apply(Resume resume, ResumeFormDTO form) {
        resume.setName(form.getName().trim());
        resume.setSummary(form.getSummary());
        resume.setSkillsDescription(form.getSkillsDescription());
        resume.setProjectsDescription(form.getProjectsDescription());
        resume.setAwardsDescription(form.getAwardsDescription());
        resume.setAboutMyself(form.getAboutMyself());
        resume.setGithubUrl(form.getGithubUrl());
        resume.setCity(form.getCity());
        resume.setRelocation(form.getRelocation());
        resume.setEnglishFluency(form.getEnglishFluency());
        resume.setContactEmail(form.getContactEmail());
        resume.setContactTelegram(form.getContactTelegram());
        resume.setContactPhone(form.getContactPhone());
        resume.setState(form.isPublished() ? PUBLISHED : DRAFT);

        // Списки приходят целиком: старые записи удаляются (orphanRemoval), новые создаются.
        resume.getWorks().clear();
        for (var w : form.getWorks() == null ? List.<ResumeWorkFormDTO>of() : form.getWorks()) {
            var work = new ResumeWork();
            work.setResume(resume);
            work.setCompany(w.getCompany().trim());
            work.setPosition(w.getPosition());
            work.setBeginDate(w.getBeginDate());
            work.setEndDate(Boolean.TRUE.equals(w.getCurrent()) ? null : w.getEndDate());
            work.setCurrent(Boolean.TRUE.equals(w.getCurrent()));
            work.setDescription(w.getDescription());
            resume.getWorks().add(work);
        }
        resume.getEducations().clear();
        for (var e : form.getEducations() == null ? List.<ResumeEducationFormDTO>of() : form.getEducations()) {
            var education = new ResumeEducation();
            education.setResume(resume);
            education.setInstitution(e.getInstitution().trim());
            education.setFaculty(e.getFaculty());
            education.setBeginDate(e.getBeginDate());
            education.setEndDate(Boolean.TRUE.equals(e.getCurrent()) ? null : e.getEndDate());
            education.setCurrent(Boolean.TRUE.equals(e.getCurrent()));
            education.setDescription(e.getDescription());
            resume.getEducations().add(education);
        }
    }

    static ResumeSummaryDTO toSummary(Resume r) {
        return new ResumeSummaryDTO(r.getId(), r.getName(), r.getSummary(), r.getCity(), r.getRelocation(),
                r.getEnglishFluency(), r.getUpdatedAt(), AuthorDTO.from(r.getUser()));
    }

    private static ResumeDetailsDTO toDetails(Resume r) {
        return new ResumeDetailsDTO(r.getId(), r.getName(), r.getSummary(), r.getSkillsDescription(),
                r.getProjectsDescription(), r.getAwardsDescription(), r.getAboutMyself(), r.getGithubUrl(),
                r.getHexletUrl(), r.getContactTelegram(), r.getCity(), r.getRelocation(), r.getEnglishFluency(),
                r.getUpdatedAt(), AuthorDTO.from(r.getUser()), works(r), educations(r));
    }

    private static OwnResumeDTO toOwn(Resume r) {
        return new OwnResumeDTO(r.getId(), r.getName(), r.getSummary(), r.getSkillsDescription(),
                r.getProjectsDescription(), r.getAwardsDescription(), r.getAboutMyself(), r.getGithubUrl(),
                r.getCity(), r.getRelocation(), r.getEnglishFluency(), r.getContactEmail(), r.getContactTelegram(),
                r.getContactPhone(), PUBLISHED.equals(r.getState()), r.getUpdatedAt(), works(r), educations(r));
    }

    // Сортировка: текущее место первым, дальше от новых к старым.
    private static List<ResumeWorkDTO> works(Resume r) {
        return r.getWorks().stream()
                .sorted(Comparator.comparing(ResumeWork::getBeginDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(w -> new ResumeWorkDTO(w.getCompany(), w.getPosition(), w.getBeginDate(), w.getEndDate(),
                        w.getCurrent(), w.getDescription()))
                .toList();
    }

    private static List<ResumeEducationDTO> educations(Resume r) {
        return r.getEducations().stream()
                .sorted(Comparator.comparing(ResumeEducation::getBeginDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(e -> new ResumeEducationDTO(e.getInstitution(), e.getFaculty(), e.getBeginDate(),
                        e.getEndDate(), e.getCurrent(), e.getDescription()))
                .toList();
    }
}
