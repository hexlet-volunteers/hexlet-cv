package io.hexlet.cv.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.mail.PasswordResetMailer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiPasswordResetTest {

    private static final String EMAIL = "reset-me@example.com";
    private static final String NEW_PASSWORD = "brand-new-pass-77";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private PasswordResetMailer mailer;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        cleanup();
        var user = new User();
        user.setEmail(EMAIL);
        user.setEncryptedPassword(encoder.encode("old-password-11"));
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setRole(RoleType.CANDIDATE);
        userRepository.save(user);
    }

    @AfterEach
    void cleanup() {
        userRepository.findByEmail(EMAIL).ifPresent(userRepository::delete);
    }

    @Test
    void unknownEmailGetsSameAnswerAndNoMail() throws Exception {
        forgot("nobody-here@example.com");
        verify(mailer, never()).send(any(), anyString());
    }

    @Test
    void fullResetFlow() throws Exception {
        var token = requestToken();

        var stored = userRepository.findByEmail(EMAIL).orElseThrow().getResetPasswordToken();
        assertThat(stored).isNotBlank().isNotEqualTo(token);

        reset(token, NEW_PASSWORD).andExpect(status().isNoContent());

        var user = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(encoder.matches(NEW_PASSWORD, user.getEncryptedPassword())).isTrue();
        assertThat(user.getResetPasswordToken()).isNull();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", EMAIL, "password", NEW_PASSWORD))))
                .andExpect(status().isOk());
    }

    @Test
    void tokenWorksOnlyOnce() throws Exception {
        var token = requestToken();
        reset(token, NEW_PASSWORD).andExpect(status().isNoContent());
        reset(token, "another-pass-88").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.token").exists());
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        var token = requestToken();
        var user = userRepository.findByEmail(EMAIL).orElseThrow();
        user.setResetPasswordSentAt(LocalDateTime.now().minusHours(3));
        userRepository.save(user);

        reset(token, NEW_PASSWORD).andExpect(status().isBadRequest());
    }

    @Test
    void wrongTokenIsRejected() throws Exception {
        reset("definitely-not-a-token", NEW_PASSWORD).andExpect(status().isBadRequest());
    }

    @Test
    void weakPasswordIsRejected() throws Exception {
        var token = requestToken();
        reset(token, "short").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    private String requestToken() throws Exception {
        forgot(EMAIL);
        var link = ArgumentCaptor.forClass(String.class);
        verify(mailer).send(any(User.class), link.capture());
        Mockito.clearInvocations(mailer);
        return UriComponentsBuilder.fromUriString(link.getValue()).build().getQueryParams().getFirst("token");
    }

    private void forgot(String email) throws Exception {
        mockMvc.perform(post("/api/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isNoContent());
    }

    private org.springframework.test.web.servlet.ResultActions reset(String token, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("token", token, "password", password))));
    }
}
