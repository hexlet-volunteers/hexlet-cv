package io.hexlet.cv.reset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.model.PasswordResetToken;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.PasswordResetTokenRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.EmailService;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

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

    private void createExpiredToken(String tokenValue) {
        var token = PasswordResetToken.builder()
                .token(tokenValue)
                .user(testUser)
                .build();
        tokenRepository.saveAndFlush(token);

        jdbcTemplate.update(
                "UPDATE password_reset_tokens SET expiry_date = ? WHERE token = ?",
                LocalDateTime.now().minusMinutes(10),
                tokenValue
        );

        entityManager.clear();
    }

    @Test
    void shouldSendResetEmail() throws Exception {
        var request = Map.of("email", testEmail);

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/users/sign_in"));

        verify(emailService).sendResetEmail(eq(testEmail), anyString());
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
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.component").value("Auth/Reset"))
                .andExpect(jsonPath("$.props.errors.token").exists());

        assertThat(tokenRepository.findByToken("expired-token")).isEmpty();
    }

    @Test
    void shouldPerformFullResetFlow() throws Exception {

        mockMvc.perform(post("/auth/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("email", testEmail)))
                .header("X-Inertia", "true"));

        String tokenValue = tokenRepository.findAll().get(0).getToken();

        mockMvc.perform(get("/auth/reset")
                        .param("token", tokenValue)
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.isValid").value(true))
                .andExpect(jsonPath("$.props.token").value(tokenValue));

        var resetRequest = Map.of(
                "password", "NewSecurePass123!",
                "confirmPassword", "NewSecurePass123!",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", tokenValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(resetRequest))
                        .header("X-Inertia", "true"))
                .andExpect(status().is3xxRedirection());

        User updatedUser = userRepository.findByEmail(testEmail).get();
        assertThat(passwordEncoder.matches("NewSecurePass123!", updatedUser.getEncryptedPassword())).isTrue();
        assertThat(tokenRepository.findByToken(tokenValue)).isEmpty();
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
    void shouldNotSendEmailForInvalidEmail() throws Exception {
        var request = Map.of("email", "invalid");

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true")
                        .header("Referer", "/auth/forgot"))
                .andExpect(status().isSeeOther())
                .andExpect(flash().attributeExists("errors"));

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
    void shouldNotResetPasswordWithWeakPassword() throws Exception {
        createValidToken("valid-token");

        var request = Map.of(
                "password", "123",
                "confirmPassword", "123",
                "autoGenerate", false
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "valid-token")
                        .header("Referer", "/auth/reset?token=valid-token")
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location",
                        "/auth/reset?token=valid-token"))
                .andExpect(flash().attributeExists("errors"));

        assertThat(tokenRepository.findByToken("valid-token")).isPresent();
    }

    @Test
    void shouldResetPasswordWithAutoGenerate() throws Exception {
        createValidToken("valid-token");

        var request = Map.of(
                "autoGenerate", true
        );

        mockMvc.perform(post("/auth/reset")
                        .param("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request))
                        .header("X-Inertia", "true"))
                .andExpect(status().is3xxRedirection());

        verify(emailService).sendNewPasswordEmail(eq(testEmail), anyString());
    }


    @Test
    void shouldHandleNonInertiaRequest() throws Exception {
        var request = Map.of("email", testEmail);

        mockMvc.perform(post("/auth/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }
}
