package io.hexlet.cv.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import io.hexlet.cv.mapper.RegistrationMapper;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RegistrationMapper registrationMapper;

    @AfterEach
    public void garbageDbDelete() {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {

        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testCreateUser() throws Exception {
        var data = createValidDto();

        var request = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isFound());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void testDisposableEmail() throws Exception {
        var data = createValidDto();
        data.setEmail("test@sharklasers.com");

        var request = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    public void testEmailPresentInDB() throws Exception {
        var data = createValidDto();
        var newUserData = registrationMapper.map(data);
        newUserData.setEncryptedPassword("123456");
        newUserData.setRole(RoleType.CANDIDATE);
        userRepository.save(newUserData);

        var request = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isConflict());
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn().getResponse();
    }

    @Test
    public void testNonExistentEmail() throws Exception {
        var data = createValidDto();
        data.setEmail("test@goopmal.com");

        var request = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    public void testNotCorrectEmail() throws Exception {
        var data = createValidDto();
        data.setEmail("testgmail.com");

        var request = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.email").exists());
    }
    // email с односимвольным TLD ------------
    // проверять нет смысла потому что валидация через запрос в
    // https://cloudflare-dns.com/ отсечет такие email
    // смысл только если запрос не пройдет
    // !!!!!!!!!!!!!!
    /*
    @Test
    public void testNotCorrectTLDEmail() throws Exception {
        var data = new RegInputDTO(); data.setEmail("test@gmail.a");
        data.setPassword("test_pass_123"); data.setFirstName("firstName");
        data.setLastName("lastName");

        var request = post("/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request) .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.email")
                        .value("TLD email должен содержать как минимум 2 символа")); //
     // .value("Домен в email не существует"));
    }
*/

    @Test
    public void testNotValidShortPassword() throws Exception {
        var data = createValidDto();
        data.setPassword("test_p");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    public void testSimplePassword() throws Exception {
        var data = createValidDto();
        // имя совпадает
        data.setPassword("firstName");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.password").exists());

        // фамилия совпадает
        data.setPassword("lastName");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.password").exists());

        // email совпадает
        data.setPassword("test@gmail.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.password").exists());
    }

// Inertia тесты ---------
    // исключение при регистрации цже имеющегося в базе юзера
    // проверяем что 303, что редирект и что флэш сообщения передаются
    @Test

    void testInertiaDuplicateEmail() throws Exception {
        var existing = new User();
        existing.setEmail("test@google.com");
        existing.setFirstName("testFirstName");
        existing.setLastName("testLastName");
        existing.setEncryptedPassword("password");
        existing.setRole(RoleType.CANDIDATE);

        userRepository.save(existing);

        var dto = createValidDto();
        dto.setEmail("test@google.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("email")));
    }


    @Test
    void testInertiaSimplePassword() throws Exception {
        // имя совпадает
        var dto = createValidDto();
        dto.setPassword("firstName");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("password")));
    }

    @Test
    void testInertiaNotValidShortPassword() throws Exception {
        var dto = createValidDto();
        dto.setPassword("test_p");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("password")));
    }

    @Test
    void testInertiaNotCorrectEmail() throws Exception {
        var dto = createValidDto();
        dto.setEmail("testgmail.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("email")));
    }

    @Test
    void testInertiaNonExistentEmail() throws Exception {
        var dto = createValidDto();
        dto.setEmail("test@goopmal.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("email")));
    }

    @Test
    void testInertiaDisposableEmail() throws Exception {
        var dto = createValidDto();
        dto.setEmail("test@sharklasers.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/users/sign_up"))
                .andExpect(flash().attributeExists("errors"))
                .andExpect(flash().attribute("errors", hasKey("email")));
    }

    @Test
    void testInertiaRegistrationUserCookies() throws Exception {
        var dto = createValidDto();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto))
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_up"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/dashboard"))
                .andExpect(flash().attributeCount(0))   // успех — без flash
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,
                        Matchers.hasItem(Matchers.containsString("access_token"))))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,
                        Matchers.hasItem(Matchers.containsString("refresh_token"))));
    }

    private RegistrationRequestDTO createValidDto() {
        var dto = new RegistrationRequestDTO();
        dto.setEmail("test@gmail.com");
        dto.setPassword("test_password123");
        dto.setFirstName("firstName");
        dto.setLastName("lastName");
        return dto;
    }
}
