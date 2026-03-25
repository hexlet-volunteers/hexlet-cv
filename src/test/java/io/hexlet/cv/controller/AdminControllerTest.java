package io.hexlet.cv.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "test_admin@example.com";
    private static final String CANDIDATE_EMAIL = "candidat_user@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        var admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setEncryptedPassword(encoder.encode("my_password"));
        admin.setRole(RoleType.ADMIN);
        userRepository.save(admin);

        var candidate = new User();
        candidate.setEmail(CANDIDATE_EMAIL);
        candidate.setEncryptedPassword(encoder.encode("candidat_password_test"));
        candidate.setRole(RoleType.CANDIDATE);
        userRepository.save(candidate);
    }

    @AfterAll
    void clearBase() {
        userRepository.deleteAll();
    }

    @Test
    void testAdminAccessPanel() throws Exception {
        // создаём access_token для ADMIN
        var token = jwtUtils.generateAccessToken(ADMIN_EMAIL);

        mockMvc.perform(get("/admin")
                        .cookie(new Cookie("access_token", token))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testCandidateAccessAdminPanel() throws Exception {
        var token = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);

        mockMvc.perform(get("/admin")
                        .cookie(new Cookie("access_token", token))
                        .header("X-Inertia", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAnonymousAccessAdminPanel() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("X-Inertia", "true"))
                .andExpect(status().is4xxClientError());
    }
}
