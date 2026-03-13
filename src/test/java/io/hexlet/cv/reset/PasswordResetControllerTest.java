package io.hexlet.cv.reset;

import io.hexlet.cv.service.EmailService;
import jakarta.servlet.http.Cookie;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private final String locale = "en";

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email(testEmail)
                .encryptedPassword(passwordEncoder.encode(testPassword))
                .firstName("Test")
                .lastName("User")
                .role(RoleType.GUEST)
                .state("active")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(testUser);

        doNothing().when(emailService).sendResetEmail(anyString(), anyString());
        doNothing().when(emailService).sendNewPasswordEmail(anyString(), anyString());
    }

    private PasswordResetToken createValidToken(String tokenValue) {
        var token = PasswordResetToken.builder()
                .token(tokenValue)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();
        return tokenRepository.save(token);
    }

    private PasswordResetToken createExpiredToken(String tokenValue) {
        var token = PasswordResetToken.builder()
                .token(tokenValue)
                .user(testUser)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();
        return tokenRepository.save(token);
    }

    @Test
    void shouldShowForgotPage() throws Exception {
        mockMvc.perform(get("/auth/forgot")
                        .header("X-Inertia", "true")
                        .cookie(new Cookie("locale", locale)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Forgot"));
    }

    @Test
    void shouldSendResetEmail() throws Exception {
        var request = Map.of("email", testEmail, "clientUrl", "http://localhost:3000/reset");

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true")
                        .cookie(new Cookie("locale", locale)))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/users/sign_in"));

        verify(emailService).sendResetEmail(eq(testEmail), anyString());
    }

    @Test
    void shouldNotSendEmailForInvalidEmail() throws Exception {
        var request = Map.of("email", "invalid", "clientUrl", "http://localhost:3000/reset");

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.errors.email").exists());

        verify(emailService, never()).sendResetEmail(anyString(), anyString());
    }


    @Test
    void shouldShowResetPageWithValidToken() throws Exception {
        createValidToken("valid-token");

        mockMvc.perform(get("/auth/reset")
                        .param("token", "valid-token")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Reset"))
                .andExpect(jsonPath("$.props.isValid").value(true));
    }

    @Test
    void shouldShowResetPageWithInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/reset")
                        .param("token", "invalid")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Reset"))
                .andExpect(jsonPath("$.props.isValid").value(false))
                .andExpect(jsonPath("$.props.errors.token").exists());
    }


    @Test
    void shouldResetPasswordWithValidToken() throws Exception {
        createValidToken("valid-token");

        var request = Map.of(
                "password", "NewPassword123!",
                "confirmPassword", "NewPassword123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/users/sign_in"));

        User updatedUser = userRepository.findByEmail(testEmail).orElseThrow();
        assertThat(passwordEncoder.matches("NewPassword123!", updatedUser.getEncryptedPassword())).isTrue();
        assertThat(tokenRepository.findByToken("valid-token")).isEmpty();
    }

    @Test
    void shouldNotResetPasswordWithInvalidToken() throws Exception {
        var request = Map.of(
                "password", "NewPassword123!",
                "confirmPassword", "NewPassword123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.errors.token").exists());
    }

    @Test
    void shouldNotResetPasswordWithExpiredToken() throws Exception {
        createExpiredToken("expired-token");

        var request = Map.of(
                "password", "NewPassword123!",
                "confirmPassword", "NewPassword123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.errors.token").exists());

        assertThat(tokenRepository.findByToken("expired-token")).isEmpty();
    }

    @Test
    void shouldNotResetPasswordWithWeakPassword() throws Exception {
        createValidToken("valid-token");

        var request = Map.of(
                "password", "123",
                "confirmPassword", "123",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/"));

        assertThat(tokenRepository.findByToken("valid-token")).isPresent();
    }


    @Test
    void shouldHandleNonInertiaRequest() throws Exception {
        var request = Map.of("email", testEmail, "clientUrl", "http://localhost:3000/reset");

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
