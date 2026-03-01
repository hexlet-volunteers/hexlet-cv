package io.hexlet.cv.reset;

import io.hexlet.cv.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.PasswordResetToken;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PasswordResetControllerTest {

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper om;

    private User testUser;
    private final String testEmail = "test@example.com";
    private final String testPassword = "Password123!";

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setEncryptedPassword(passwordEncoder.encode(testPassword));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(RoleType.GUEST);
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(testUser);
    }

    @Test
    void testForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/ru/auth/forgot")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Forgot"));
    }

    @Test
    void testSendResetEmail() throws Exception {
        var request = Map.of("email", testEmail);

        mockMvc.perform(post("/ru/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "ru/users/sign_in"));
    }

    @Test
    void testResetPasswordPageWithValidToken() throws Exception {
        var token = createValidToken("valid-token");

        mockMvc.perform(get("/ru/auth/reset")
                        .param("token", "valid-token")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Reset"))
                .andExpect(jsonPath("$.props.isValid").value(true));
    }


    @Test
    void testResetPasswordPageWithInvalidToken() throws Exception {
        mockMvc.perform(get("/ru/auth/reset")
                        .param("token", "invalid-token")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Reset"))
                .andExpect(jsonPath("$.props.isValid").value(false));
    }


    @Test
    void testResetPasswordSuccess() throws Exception {
        var token = createValidToken("valid-token");

        var request = Map.of(
                "password", "NewPassword123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/ru/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/"));
    }

    @Test
    void testResetPasswordWithExpiredToken() throws Exception {
        var token = createExpiredToken("expired-token");

        var request = Map.of(
                "password", "NewPassword123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/ru/auth/reset")
                        .param("token", "expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("token")));
    }

    @Test
    void testResetPasswordWithEmptyPassword() throws Exception {
        var token = createValidToken("valid-token");

        var request = Map.of(
                "password", "",
                "autoGenerate", false
        );

        mockMvc.perform(post("/ru/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("password")));
    }

    @Test
    void testResetPasswordWithWeakPassword() throws Exception {
        var token = createValidToken("valid-token");

        var request = Map.of(
                "password", "123",
                "autoGenerate", false
        );

        mockMvc.perform(post("/ru/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("password")));

    }

    private PasswordResetToken createValidToken(String tokenValue) {
        var token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(testUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);
    }

    private PasswordResetToken createExpiredToken(String tokenValue) {
        var token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(testUser);
        token.setExpiryDate(LocalDateTime.now().minusHours(1));
        return tokenRepository.save(token);
    }
}
