package io.hexlet.cv.controller.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.support.TokenTestHelper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiAuthControllerTest {

    private static final String EMAIL = "api-auth@example.com";
    private static final String PASSWORD = "test_password123";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private TokenTestHelper tokenHelper;

    @Autowired
    private ObjectMapper om;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        cleanup();
    }

    @AfterEach
    void cleanup() {
        userRepository.findByEmail(EMAIL).ifPresent(userRepository::delete);
        userRepository.findByEmail("api-new@gmail.com").ifPresent(userRepository::delete);
    }

    @Test
    void loginReturnsUserAndSetsCookies() throws Exception {
        createUser();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("access_token"))))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("refresh_token"))));
    }

    @Test
    void staleAccessCookieDoesNotBlockLogin() throws Exception {
        createUser();

        mockMvc.perform(post("/api/auth/login")
                        .cookie(new Cookie("access_token", "stale.or.broken.token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        createUser();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", EMAIL, "password", "wrong_password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors.password").value("Неверный пароль"));
    }

    @Test
    void registerCreatesUserAndLogsIn() throws Exception {
        var body = Map.of(
                "email", "api-new@gmail.com",
                "password", PASSWORD,
                "firstName", "Анна",
                "lastName", "Смирнова",
                "terms", true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("api-new@gmail.com"))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("access_token"))));
    }

    @Test
    void meRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsCurrentUser() throws Exception {
        createUser();
        var token = tokenHelper.accessToken(EMAIL, PASSWORD);

        mockMvc.perform(get("/api/auth/me").cookie(new Cookie("access_token", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value("CANDIDATE"));
    }

    @Test
    void logoutExpiresCookies() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("Max-Age=0"))));
    }

    private void createUser() {
        var user = new User();
        user.setEmail(EMAIL);
        user.setEncryptedPassword(encoder.encode(PASSWORD));
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setRole(RoleType.CANDIDATE);
        userRepository.save(user);
    }
}
