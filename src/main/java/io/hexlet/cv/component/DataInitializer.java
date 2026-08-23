package io.hexlet.cv.component;

import io.hexlet.cv.model.Career;
import io.hexlet.cv.model.CareerItem;
import io.hexlet.cv.model.CareerMember;
import io.hexlet.cv.model.CareerMemberVersion;
import io.hexlet.cv.model.CareerStep;
import io.hexlet.cv.model.CareerStepMember;
import io.hexlet.cv.model.Country;
import io.hexlet.cv.model.Event;
import io.hexlet.cv.model.Impression;
import io.hexlet.cv.model.NewsletterSettings;
import io.hexlet.cv.model.Notification;
import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.ResumeAnswer;
import io.hexlet.cv.model.ResumeAnswerComment;
import io.hexlet.cv.model.ResumeAnswerLike;
import io.hexlet.cv.model.ResumeComment;
import io.hexlet.cv.model.ResumeEducation;
import io.hexlet.cv.model.ResumeWork;
import io.hexlet.cv.model.Tag;
import io.hexlet.cv.model.Tagging;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.Vacancy;
import io.hexlet.cv.model.Version;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.CareerItemRepository;
import io.hexlet.cv.repository.CareerMemberRepository;
import io.hexlet.cv.repository.CareerMemberVersionRepository;
import io.hexlet.cv.repository.CareerRepository;
import io.hexlet.cv.repository.CareerStepMemberRepository;
import io.hexlet.cv.repository.CareerStepRepository;
import io.hexlet.cv.repository.CountryRepository;
import io.hexlet.cv.repository.EventRepository;
import io.hexlet.cv.repository.ImpressionRepository;
import io.hexlet.cv.repository.NewsletterSettingsRepository;
import io.hexlet.cv.repository.NotificationRepository;
import io.hexlet.cv.repository.ResumeAnswerCommentRepository;
import io.hexlet.cv.repository.ResumeAnswerLikeRepository;
import io.hexlet.cv.repository.ResumeAnswerRepository;
import io.hexlet.cv.repository.ResumeCommentRepository;
import io.hexlet.cv.repository.ResumeEducationRepository;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.ResumeWorkRepository;
import io.hexlet.cv.repository.TagRepository;
import io.hexlet.cv.repository.TaggingRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.repository.VacancyRepository;
import io.hexlet.cv.repository.VersionRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeWorkRepository resumeWorkRepository;
    private final ResumeEducationRepository resumeEducationRepository;
    private final ResumeAnswerRepository resumeAnswerRepository;
    private final ResumeCommentRepository resumeCommentRepository;
    private final ResumeAnswerCommentRepository resumeAnswerCommentRepository;
    private final ResumeAnswerLikeRepository resumeAnswerLikeRepository;
    private final TagRepository tagRepository;
    private final TaggingRepository taggingRepository;
    private final CareerRepository careerRepository;
    private final CareerStepRepository careerStepRepository;
    private final CareerItemRepository careerItemRepository;
    private final CareerMemberRepository careerMemberRepository;
    private final CareerStepMemberRepository careerStepMemberRepository;
    private final CareerMemberVersionRepository careerMemberVersionRepository;
    private final ImpressionRepository impressionRepository;
    private final VacancyRepository vacancyRepository;
    private final CountryRepository countryRepository;
    private final NotificationRepository notificationRepository;
    private final EventRepository eventRepository;
    private final VersionRepository versionRepository;
    private final PasswordEncoder passwordEncoder;
    private final NewsletterSettingsRepository newsletterSettingsRepository;

    @PostConstruct
    public void initializeData() {
        if (userRepository.count() > 0) {
            return;
        }

        //+уведомления
        User ivan = createUser("ivan@google.com", "Иван", "Иванов", RoleType.CANDIDATE);
        createNewsletterSettings(ivan);

        User olga = createUser("olga@yandex.ru", "Ольга", "Петрова", RoleType.CANDIDATE);
        createNewsletterSettings(olga);

        User sergey = createUser("sergey@gmail.com", "Сергей", "Сидоров", RoleType.ADMIN);
        createNewsletterSettings(sergey);

        // резюме
        Resume ivanResume = createResume(ivan, "Резюме backend-разработчика");
        Resume olgaResume = createResume(olga, "Резюме frontend-разработчика");

        // работа образование
        createResumeWork(ivanResume, "Hexlet", "Java Developer");
        createResumeEducation(ivanResume, "МГУ", "Факультет ВМК");

        createResumeWork(olgaResume, "Яндекс", "Frontend Developer");
        createResumeEducation(olgaResume, "СПбГУ", "Прикладная математика");

        // ответы рекомендации
        ResumeAnswer answer = createResumeAnswer(
                olgaResume, ivan, "Отличное резюме, но стоит уточнить опыт в Spring Boot.");
        createResumeAnswerLike(answer, sergey);
        createResumeAnswerComment(answer, sergey, ivan, "Полностью согласен, добавить детали по проектам.");

        // комменты
        createResumeComment(ivanResume, sergey, "Хорошее резюме, но желательно добавить больше деталей об опыте.");

        // карбера
        Career career = createCareer("Java разработчик");
        CareerStep step1 = createCareerStep("Основы Java", "Изучение базового синтаксиса");
        CareerItem item1 = createCareerItem(career, step1, 1);

        CareerMember cm = createCareerMember(career, ivan);
        createCareerStepMember(step1, cm);

        //
        Country russia = createCountry("Россия");
        Vacancy vacancy = createVacancy(sergey, "Java Developer в Hexlet", russia);

        //
        createEvent(ivan, "resume_created", "Resume", ivanResume.getId());
        createNotification(ivan, "Resume", ivanResume.getId(), "new_comment");

        //
        createImpression(ivan, "ResumesController", "show", "resume_view");

        //
        Tag javaTag = createTag("Java");
        createTagging(javaTag, "Resume", ivanResume.getId());

        //
        createVersion("Resume", ivanResume.getId(), "create", "system");
        createCareerMemberVersion(cm, "create", "system");


        var users = userRepository.findAll();

        System.out.println();
        System.out.println(" ----------------------------------------------");
        users.stream()
                .forEach(user -> {
                    System.out.println("User ID: " + user.getId() + " email: "
                            + user.getEmail() + " password: qweqweqwe");
                    if (user.getNewsletterSettings() != null) {
                        System.out.println("   Newsletter settings created");
                    }
                });
        System.out.println(" ----------------------------------------------");
        System.out.println();

    }

    // ==== PRIVATE HELPERS ====
    private User createUser(String email, String first, String last, RoleType role) {
        User u = new User();
        u.setEmail(email);
        u.setFirstName(first);
        u.setLastName(last);
        u.setEncryptedPassword(passwordEncoder.encode("qweqweqwe"));
        u.setSignInCount(1);
        u.setCurrentSignInAt(LocalDateTime.now());
        u.setCurrentSignInIp("127.0.0.1");
        u.setRole(role);
        u.setState("active");
        u.setLocale("ru");
        return userRepository.save(u);
    }

    private Resume createResume(User user, String name) {
        Resume r = new Resume();
        r.setUser(user);
        r.setName(name);
        r.setState("draft");
        r.setAnswersCount(0);
        r.setLocale("ru");
        r.setCity("Москва");
        r.setRelocation("возможен");
        r.setContact("telegram");
        r.setContactPhone("+7-900-123-45-67");
        r.setContactEmail(user.getEmail());
        r.setContactTelegram("@" + user.getFirstName().toLowerCase());
        r.setEvaluatedAi(false);
        r.setSummary("Это резюме " + user.getFirstName() + " " + user.getLastName() + " тут тело самого резюме лалала");
        return resumeRepository.save(r);
    }

    private ResumeWork createResumeWork(Resume resume, String company, String position) {
        ResumeWork w = new ResumeWork();
        w.setResume(resume);
        w.setCompany(company);
        w.setPosition(position);
        w.setBeginDate(LocalDate.now().minusYears(2));
        w.setEndDate(LocalDate.now());
        w.setCurrent(false);
        w.setDescription("Работа в проекте");
        w.setCompanyDescription("Крупная компания");
        return resumeWorkRepository.save(w);
    }

    private ResumeEducation createResumeEducation(Resume resume, String inst, String faculty) {
        ResumeEducation e = new ResumeEducation();
        e.setResume(resume);
        e.setInstitution(inst);
        e.setFaculty(faculty);
        e.setBeginDate(LocalDate.now().minusYears(6));
        e.setEndDate(LocalDate.now().minusYears(2));
        e.setCurrent(false);
        e.setDescription("Обучение в университете");
        return resumeEducationRepository.save(e);
    }

    private ResumeAnswer createResumeAnswer(Resume resume, User user, String content) {
        ResumeAnswer a = new ResumeAnswer();
        a.setResume(resume);
        a.setUser(user);
        a.setContent(content);
        a.setLikesCount(0);
        a.setApplyingState("reviewed");
        return resumeAnswerRepository.save(a);
    }

    private ResumeAnswerLike createResumeAnswerLike(ResumeAnswer answer, User user) {
        ResumeAnswerLike l = new ResumeAnswerLike();
        l.setAnswer(answer);
        l.setResume(answer.getResume());
        l.setUser(user);
        return resumeAnswerLikeRepository.save(l);
    }

    private ResumeAnswerComment createResumeAnswerComment(
            ResumeAnswer answer, User user, User answerUser, String content) {
        ResumeAnswerComment c = new ResumeAnswerComment();
        c.setAnswer(answer);
        c.setResume(answer.getResume());
        c.setUser(user);
        c.setAnswerUser(answerUser);
        c.setContent(content);
        return resumeAnswerCommentRepository.save(c);
    }

    private ResumeComment createResumeComment(Resume resume, User user, String content) {
        ResumeComment c = new ResumeComment();
        c.setResume(resume);
        c.setUser(user);
        c.setContent(content);
        return resumeCommentRepository.save(c);
    }

    private Career createCareer(String name) {
        Career c = new Career();
        c.setName(name);
        c.setDescription("Путь развития разработчика");
        c.setSlug("java-dev");
        c.setLocale("ru");
        return careerRepository.save(c);
    }

    private CareerStep createCareerStep(String name, String desc) {
        CareerStep s = new CareerStep();
        s.setName(name);
        s.setDescription(desc);
        s.setTasksText("Пройти курс, выполнить задания");
        s.setReviewNeeded(true);
        s.setLocale("ru");
        s.setNotificationKind("email");
        return careerStepRepository.save(s);
    }

    private CareerItem createCareerItem(Career career, CareerStep step, int order) {
        CareerItem i = new CareerItem();
        i.setCareer(career);
        i.setCareerStep(step);
        i.setOrderIndex(order);
        return careerItemRepository.save(i);
    }

    private CareerMember createCareerMember(Career career, User user) {
        CareerMember cm = new CareerMember();
        cm.setCareer(career);
        cm.setUser(user);
        cm.setState("in_progress");
        cm.setFinishedAt(null);
        return careerMemberRepository.save(cm);
    }

    private CareerStepMember createCareerStepMember(CareerStep step, CareerMember cm) {
        CareerStepMember sm = new CareerStepMember();
        sm.setCareerStep(step);
        sm.setCareerMember(cm);
        sm.setState("started");
        return careerStepMemberRepository.save(sm);
    }

    private Country createCountry(String name) {
        Country c = new Country();
        c.setName(name);
        return countryRepository.save(c);
    }

    private Vacancy createVacancy(User creator, String title, Country country) {
        Vacancy v = new Vacancy();
        v.setCreator(creator);
        v.setTitle(title);
        v.setKind("fulltime");
        v.setState("published");
        v.setCompanyName("Hexlet");
        v.setProgrammingLanguage("Java");
        v.setLocation("Москва");
        v.setCityName("Москва");
        v.setCountry(country);
        v.setSalaryFrom(150000);
        v.setSalaryTo(250000);
        v.setEmploymentType("office");
        v.setPositionLevel("middle");
        v.setSalaryCurrency("RUB");
        v.setSalaryAmountType("gross");
        v.setLocale("ru");
        v.setPublishedAt(LocalDateTime.now());
        return vacancyRepository.save(v);
    }

    private Event createEvent(User user, String kind, String resourceType, Long resourceId) {
        Event e = new Event();
        e.setUser(user);
        e.setKind(kind);
        e.setLocale("ru");
        e.setState("done");
        e.setResourceType(resourceType);
        e.setResourceId(resourceId);
        return eventRepository.save(e);
    }

    private Notification createNotification(User user, String resourceType, Long resourceId, String kind) {
        Notification n = new Notification();
        n.setUser(user);
        n.setResourceType(resourceType);
        n.setResourceId(resourceId);
        n.setState("new");
        n.setKind(kind);
        return notificationRepository.save(n);
    }

    private Impression createImpression(User user, String controller, String action, String view) {
        Impression i = new Impression();
        i.setUser(user);
        i.setControllerName(controller);
        i.setActionName(action);
        i.setViewName(view);
        i.setImpressionableType("Resume");
        i.setImpressionableId(1L);
        i.setIpAddress("127.0.0.1");
        i.setSessionHash("sess-123");
        i.setRequestHash("req-123");
        i.setMessage("Просмотр резюме");
        return impressionRepository.save(i);
    }

    private Tag createTag(String name) {
        Tag t = new Tag();
        t.setName(name);
        t.setTaggingsCount(1);
        return tagRepository.save(t);
    }

    private Tagging createTagging(Tag tag, String type, Long id) {
        Tagging tg = new Tagging();
        tg.setTag(tag);
        tg.setTaggableType(type);
        tg.setTaggableId(id);
        tg.setTaggerType("User");
        tg.setTaggerId(1L);
        tg.setContext("default");
        tg.setTenant("default");
        return taggingRepository.save(tg);
    }

    private Version createVersion(String type, Long id, String event, String who) {
        Version v = new Version();
        v.setItemType(type);
        v.setItemId(id);
        v.setEvent(event);
        v.setWhodunnit(who);
        v.setObject("object-data");
        return versionRepository.save(v);
    }

    private CareerMemberVersion createCareerMemberVersion(
            CareerMember careerMember, String event, String who) {
        CareerMemberVersion cv = new CareerMemberVersion();
        cv.setItemType("CareerMember");
        cv.setItemId(careerMember.getId());
        cv.setEvent(event);
        cv.setWhodunnit(who);
        cv.setCareerMember(careerMember);
        return careerMemberVersionRepository.save(cv);
    }

    private NewsletterSettings createNewsletterSettings(User user) {
        NewsletterSettings settings = NewsletterSettings.builder()
                .user(user)
                .newCourses(true)
                .courseUpdates(true)
                .promotions(user.getRole() == RoleType.ADMIN)
                .achievements(true)
                .commentsReplies(true)
                .resumeViews(true)
                .vacancyMatches(true)
                .communityNews(user.getRole() == RoleType.ADMIN)
                .marketingTips(false)
                .build();
        return newsletterSettingsRepository.save(settings);
    }
}
