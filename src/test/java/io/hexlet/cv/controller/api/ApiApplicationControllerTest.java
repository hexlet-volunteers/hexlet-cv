package io.hexlet.cv.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.JobApplicationRepository;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.support.TokenTestHelper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ApiApplicationControllerTest {

    private static final String PASSWORD = "password";
    private static final String BASE = "/api/account/applications";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private TokenTestHelper tokenHelper;

    @Autowired
    private ObjectMapper om;

    private MockMvc mockMvc;
    private User user;
    private User stranger;
    private Resume resume;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .defaultRequest(get("/").with(csrf()))
                .build();
        user = createUser("tracker@example.com");
        stranger = createUser("tracker-stranger@example.com");
        var r = new Resume();
        r.setUser(user);
        r.setName("v3 · Продуктовые");
        r.setState("draft");
        r.setAnswersCount(0);
        resume = resumeRepository.save(r);
    }

    @AfterEach
    void tearDown() {
        for (var u : new User[] {user, stranger}) {
            applicationRepository.deleteAll(applicationRepository.findByUserIdOrderByLastEventAtDesc(u.getId()));
            resumeRepository.deleteAll(resumeRepository.findByUserId(u.getId()));
            userRepository.delete(u);
        }
    }

    @Test
    void createsSavedApplicationWithTimelineEvent() throws Exception {
        create(Map.of("company", "Т-Банк", "position", "Frontend-разработчик", "resumeId", resume.getId(),
                "source", "Хабр Карьера", "salary", "от 140 тыс ₽"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stage").value("SAVED"))
                .andExpect(jsonPath("$.resume.name").value("v3 · Продуктовые"))
                .andExpect(jsonPath("$.events.length()").value(1))
                .andExpect(jsonPath("$.events[0].type").value("CREATED"))
                .andExpect(jsonPath("$.events[0].text").value("Вакансия сохранена в трекер · источник: Хабр Карьера"));
    }

    @Test
    void requiresCompanyAndPosition() throws Exception {
        create(Map.of("company", " ")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.company").exists())
                .andExpect(jsonPath("$.errors.position").exists());
    }

    @Test
    void rejectsForeignResume() throws Exception {
        var foreign = new Resume();
        foreign.setUser(stranger);
        foreign.setName("чужое");
        foreign.setState("draft");
        foreign.setAnswersCount(0);
        foreign = resumeRepository.save(foreign);

        create(Map.of("company", "А", "position", "Б", "resumeId", foreign.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.resumeId").exists());
    }

    @Test
    void listsWithCountsFiltersAndSearch() throws Exception {
        create(Map.of("company", "Ozon Tech", "position", "Junior Frontend", "stage", "APPLIED",
                "resumeId", resume.getId()));
        create(Map.of("company", "Контур", "position", "Junior Frontend", "stage", "SCREENING"));
        create(Map.of("company", "Циан", "position", "React-разработчик"));

        mockMvc.perform(get(BASE).cookie(token(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.counts.ALL").value(3))
                .andExpect(jsonPath("$.counts.SAVED").value(1))
                .andExpect(jsonPath("$.counts.APPLIED").value(1))
                .andExpect(jsonPath("$.counts.SCREENING").value(1));

        mockMvc.perform(get(BASE).param("stage", "SCREENING").cookie(token(user)))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].company").value("Контур"))
                .andExpect(jsonPath("$.counts.ALL").value(3));

        mockMvc.perform(get(BASE).param("q", "react").cookie(token(user)))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.counts.ALL").value(1));

        mockMvc.perform(get(BASE).param("resumeId", resume.getId().toString()).cookie(token(user)))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].company").value("Ozon Tech"));

        mockMvc.perform(get(BASE).cookie(token(stranger)))
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void stageChangeIsLoggedAndRejectionsCounted() throws Exception {
        var id = idOf(create(Map.of("company", "Авито", "position", "Junior Frontend", "stage", "INTERVIEW")));

        stage(id, Map.of("stage", "OFFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stage").value("OFFER"))
                .andExpect(jsonPath("$.events[1].type").value("STAGE_CHANGED"))
                .andExpect(jsonPath("$.events[1].text").value("Этап: Оффер"));

        stage(id, Map.of("stage", "REJECTED", "rejectedAtStage", "INTERVIEW"))
                .andExpect(jsonPath("$.rejectedAtStage").value("INTERVIEW"))
                .andExpect(jsonPath("$.events[2].text").value("Отказ после этапа «Собеседование»"));

        mockMvc.perform(get(BASE).cookie(token(user)))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.counts.ALL").value(0))
                .andExpect(jsonPath("$.rejections.total").value(1))
                .andExpect(jsonPath("$.rejections.byStage.INTERVIEW").value(1));
    }

    @Test
    void manualEventsAreAddedAndSystemOnesRefused() throws Exception {
        var id = idOf(create(Map.of("company", "Т-Банк", "position", "Frontend")));

        event(id, Map.of("type", "INTERVIEW", "text", "Техническое интервью, 60 мин, Zoom",
                "occurredAt", LocalDateTime.now().minusHours(1).withNano(0).toString().replace('T', ' ')
                        .substring(0, 16)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[1].text").value("Техническое интервью, 60 мин, Zoom"));

        event(id, Map.of("type", "STAGE_CHANGED", "text", "подделка"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.type").exists());
    }

    @Test
    void computesSilenceAndDeadlineAlerts() throws Exception {
        var silentId = idOf(create(Map.of("company", "МТС Диджитал", "position", "Frontend", "stage", "APPLIED")));
        var a = applicationRepository.findById(silentId).orElseThrow();
        a.setLastEventAt(LocalDateTime.now().minusDays(8));
        applicationRepository.save(a);

        create(Map.of("company", "Циан", "position", "React", "deadline",
                LocalDate.now().plusDays(2).toString()));

        mockMvc.perform(get(BASE).cookie(token(user)))
                .andExpect(jsonPath("$.items[?(@.company == 'МТС Диджитал')].alerts[0].type").value("SILENCE"))
                .andExpect(jsonPath("$.items[?(@.company == 'МТС Диджитал')].alerts[0].days").value(8))
                .andExpect(jsonPath("$.items[?(@.company == 'Циан')].alerts[0].type").value("DEADLINE"))
                .andExpect(jsonPath("$.items[?(@.company == 'Циан')].alerts[0].days").value(2));
    }

    @Test
    void updateKeepsStageAndStrangerGetsNotFound() throws Exception {
        var id = idOf(create(Map.of("company", "Точка", "position", "Junior", "stage", "APPLIED")));

        mockMvc.perform(put(BASE + "/" + id).cookie(token(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("company", "Точка", "position", "Middle",
                                "nextStepTitle", "Звонок с HR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Middle"))
                .andExpect(jsonPath("$.stage").value("APPLIED"))
                .andExpect(jsonPath("$.nextStepTitle").value("Звонок с HR"));

        mockMvc.perform(get(BASE + "/" + id).cookie(token(stranger))).andExpect(status().isNotFound());
        mockMvc.perform(delete(BASE + "/" + id).cookie(token(stranger))).andExpect(status().isNotFound());
        mockMvc.perform(delete(BASE + "/" + id).cookie(token(user))).andExpect(status().isNoContent());
        assertThat(applicationRepository.findById(id)).isEmpty();
    }

    @Test
    void requiresLogin() throws Exception {
        mockMvc.perform(get(BASE)).andExpect(status().isUnauthorized());
    }

    private ResultActions create(Map<String, ?> body) throws Exception {
        return mockMvc.perform(post(BASE).cookie(token(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(new HashMap<>(body))));
    }

    private ResultActions stage(long id, Map<String, ?> body) throws Exception {
        return mockMvc.perform(patch(BASE + "/" + id + "/stage").cookie(token(user))
                .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)));
    }

    private ResultActions event(long id, Map<String, ?> body) throws Exception {
        return mockMvc.perform(post(BASE + "/" + id + "/events").cookie(token(user))
                .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)));
    }

    private long idOf(ResultActions result) throws Exception {
        return om.readTree(result.andReturn().getResponse().getContentAsString()).get("id").asLong();
    }

    private Cookie token(User u) {
        return new Cookie("access_token", tokenHelper.accessToken(u.getEmail(), PASSWORD));
    }

    private User createUser(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            applicationRepository.deleteAll(applicationRepository.findByUserIdOrderByLastEventAtDesc(u.getId()));
            resumeRepository.deleteAll(resumeRepository.findByUserId(u.getId()));
            userRepository.delete(u);
        });
        var u = new User();
        u.setEmail(email);
        u.setEncryptedPassword(encoder.encode(PASSWORD));
        u.setFirstName("Артём");
        u.setLastName("Крылов");
        u.setRole(RoleType.CANDIDATE);
        return userRepository.save(u);
    }
}
