package io.hexlet.cv.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.support.TokenTestHelper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiResumeControllerTest {

    private static final String PASSWORD = "password";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private TokenTestHelper tokenHelper;

    @Autowired
    private ObjectMapper om;

    private MockMvc mockMvc;
    private User author;
    private User stranger;
    private Resume published;
    private Resume draft;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .defaultRequest(get("/").with(csrf()))
                .build();

        author = createUser("api-author@example.com");
        stranger = createUser("api-stranger@example.com");
        published = createResume(author, "Zeta Java Developer", "published");
        draft = createResume(author, "Zeta Draft Resume", "draft");
    }

    @AfterEach
    void tearDown() {
        resumeRepository.deleteAll(resumeRepository.findByUserId(author.getId()));
        resumeRepository.deleteAll(resumeRepository.findByUserId(stranger.getId()));
        userRepository.delete(author);
        userRepository.delete(stranger);
    }

    @Test
    void catalogShowsOnlyPublishedResumes() throws Exception {
        mockMvc.perform(get("/api/resumes").param("q", "zeta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.items[0].id").value(published.getId()))
                .andExpect(jsonPath("$.items[0].author.firstName").value("Иван"))
                .andExpect(jsonPath("$.items[0].author.email").doesNotExist());
    }

    @Test
    void publishedResumeIsPublicWithoutPrivateContacts() throws Exception {
        mockMvc.perform(get("/api/resumes/{id}", published.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zeta Java Developer"))
                .andExpect(jsonPath("$.contactPhone").doesNotExist())
                .andExpect(jsonPath("$.contactEmail").doesNotExist());
    }

    @Test
    void draftResumeIsNotPublic() throws Exception {
        mockMvc.perform(get("/api/resumes/{id}", draft.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void profileListsOnlyPublishedResumes() throws Exception {
        mockMvc.perform(get("/api/users/{id}", author.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumes.length()").value(1))
                .andExpect(jsonPath("$.resumes[*].id", not(hasItem(draft.getId().intValue()))));
    }

    @Test
    void accountRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/account/resumes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorSeesOwnResumesIncludingDrafts() throws Exception {
        mockMvc.perform(get("/api/account/resumes").cookie(tokenFor(author)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void authorCreatesAndPublishesResume() throws Exception {
        var body = Map.of("name", "Zeta New Resume", "city", "Казань", "published", true);

        var response = mockMvc.perform(post("/api/account/resumes")
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published").value(true))
                .andReturn().getResponse().getContentAsString();

        var id = om.readTree(response).get("id").asLong();
        var saved = resumeRepository.findById(id).orElseThrow();
        assertThat(saved.getState()).isEqualTo("published");
        assertThat(saved.getUser().getId()).isEqualTo(author.getId());
    }

    @Test
    void createValidatesName() throws Exception {
        mockMvc.perform(post("/api/account/resumes")
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("name", " "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void authorUpdatesOwnResume() throws Exception {
        var body = Map.of("name", "Zeta Renamed", "published", false);

        mockMvc.perform(put("/api/account/resumes/{id}", published.getId())
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zeta Renamed"))
                .andExpect(jsonPath("$.published").value(false));

        assertThat(resumeRepository.findById(published.getId()).orElseThrow().getState()).isEqualTo("draft");
    }

    @Test
    void strangerCannotEditForeignResume() throws Exception {
        mockMvc.perform(put("/api/account/resumes/{id}", published.getId())
                        .cookie(tokenFor(stranger))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("name", "Hacked"))))
                .andExpect(status().isNotFound());

        assertThat(resumeRepository.findById(published.getId()).orElseThrow().getName())
                .isEqualTo("Zeta Java Developer");
    }

    @Test
    void authorSavesWorkAndEducation() throws Exception {
        var body = Map.of(
                "name", "Zeta Java Developer",
                "published", true,
                "works", List.of(
                        Map.of("company", "Old Corp", "position", "Стажёр",
                                "beginDate", "2022-01-01", "endDate", "2023-01-01"),
                        Map.of("company", "New Corp", "position", "Junior", "beginDate", "2024-03-01",
                                "endDate", "2025-01-01", "current", true)),
                "educations", List.of(Map.of("institution", "УрФУ", "faculty", "ИРИТ-РтФ")));

        mockMvc.perform(put("/api/account/resumes/{id}", published.getId())
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.works.length()").value(2))
                .andExpect(jsonPath("$.works[0].company").value("New Corp"))
                .andExpect(jsonPath("$.works[0].endDate").doesNotExist())
                .andExpect(jsonPath("$.educations[0].institution").value("УрФУ"));

        mockMvc.perform(get("/api/resumes/{id}", published.getId()))
                .andExpect(jsonPath("$.works.length()").value(2))
                .andExpect(jsonPath("$.educations.length()").value(1));

        // Повторное сохранение заменяет список, а не дописывает его.
        mockMvc.perform(put("/api/account/resumes/{id}", published.getId())
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("name", "Zeta Java Developer", "published", true,
                                "works", List.of(Map.of("company", "Only Corp"))))))
                .andExpect(jsonPath("$.works.length()").value(1))
                .andExpect(jsonPath("$.educations.length()").value(0));
    }

    @Test
    void workWithoutCompanyIsRejected() throws Exception {
        var body = Map.of("name", "Zeta", "works", List.of(Map.of("position", "Junior")));

        mockMvc.perform(put("/api/account/resumes/{id}", published.getId())
                        .cookie(tokenFor(author))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['works[0].company']").exists());
    }

    private Cookie tokenFor(User user) {
        return new Cookie("access_token", tokenHelper.accessToken(user.getEmail(), PASSWORD));
    }

    private User createUser(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            resumeRepository.deleteAll(resumeRepository.findByUserId(u.getId()));
            userRepository.delete(u);
        });
        var user = new User();
        user.setEmail(email);
        user.setEncryptedPassword(encoder.encode(PASSWORD));
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setRole(RoleType.CANDIDATE);
        return userRepository.save(user);
    }

    private Resume createResume(User user, String name, String state) {
        var resume = new Resume();
        resume.setUser(user);
        resume.setName(name);
        resume.setState(state);
        resume.setAnswersCount(0);
        resume.setContactPhone("+7 900 000-00-00");
        resume.setContactEmail("private@example.com");
        return resumeRepository.save(resume);
    }
}
