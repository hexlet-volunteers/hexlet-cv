package io.hexlet.cv.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class LegacyRedirectControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    private MockMvc mockMvc;
    private User user;
    private Resume published;
    private Resume draft;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        userRepository.findByEmail("legacy@example.com").ifPresent(this::remove);
        user = new User();
        user.setEmail("legacy@example.com");
        user.setFirstName("Старый");
        user.setLastName("Пользователь");
        user.setRole(RoleType.CANDIDATE);
        user = userRepository.save(user);
        published = resume("published");
        draft = resume("draft");
    }

    @AfterEach
    void tearDown() {
        remove(user);
    }

    @Test
    void resumeRedirectsPermanently() throws Exception {
        mockMvc.perform(get("/ru/resumes/{id}", published.getId()))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/resumes/" + published.getId()));
        mockMvc.perform(get("/en/resumes/{id}", published.getId()))
                .andExpect(status().isMovedPermanently());
    }

    @Test
    void hiddenOrMissingResumeIsNotFound() throws Exception {
        mockMvc.perform(get("/ru/resumes/{id}", draft.getId())).andExpect(status().isNotFound());
        mockMvc.perform(get("/ru/resumes/999999999")).andExpect(status().isNotFound());
    }

    @Test
    void userRedirectsToProfile() throws Exception {
        mockMvc.perform(get("/ru/users/{id}", user.getId()))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/profiles/" + user.getId()));
        mockMvc.perform(get("/ru/users/999999999")).andExpect(status().isNotFound());
    }

    @Test
    void catalogKeepsSearchAndPage() throws Exception {
        mockMvc.perform(get("/ru").param("q", "java").param("page", "3"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/resumes?q=java&page=3"));
        mockMvc.perform(get("/ru/"))
                .andExpect(header().string("Location", "/resumes"));
    }

    @Test
    void catalogUnderstandsRansackFilter() throws Exception {
        mockMvc.perform(get("/ru").param("q[name_cont]", "Казань"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/resumes?q=%D0%9A%D0%B0%D0%B7%D0%B0%D0%BD%D1%8C"));
    }

    @Test
    void authPagesRedirect() throws Exception {
        mockMvc.perform(get("/ru/users/sign_in")).andExpect(header().string("Location", "/login"));
        mockMvc.perform(get("/en/users/sign_up")).andExpect(header().string("Location", "/register"));
        mockMvc.perform(get("/ru/users/password/new"))
                .andExpect(header().string("Location", "/password/forgot"));
        mockMvc.perform(get("/ru/vacancies")).andExpect(header().string("Location", "/"));
    }

    private Resume resume(String state) {
        var r = new Resume();
        r.setUser(user);
        r.setName("Legacy " + state);
        r.setState(state);
        r.setAnswersCount(0);
        return resumeRepository.save(r);
    }

    private void remove(User u) {
        resumeRepository.deleteAll(resumeRepository.findByUserId(u.getId()));
        userRepository.delete(u);
    }
}
