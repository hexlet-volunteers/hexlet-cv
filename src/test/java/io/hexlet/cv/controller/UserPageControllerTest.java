package io.hexlet.cv.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class UserPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testUserPagePropsOk() throws Exception {
        var user = new User();
        user.setEmail("test@google.com");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setEncryptedPassword("123456");
        user.setRole(RoleType.CANDIDATE);
        var saved = userRepository.save(user);

        mockMvc.perform(get("/users/" + saved.getId())
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.props.id").value(saved.getId()))
                .andExpect(jsonPath("$.props.firstName").value("testFirstName"))
                .andExpect(jsonPath("$.props.lastName").value("testLastName"))
                .andExpect(jsonPath("$.props.role").value(RoleType.CANDIDATE.name().toLowerCase()))

                .andExpect(jsonPath("$.props.resumes", hasSize(0)))
                .andExpect(jsonPath("$.props.recentAnswers", hasSize(0)))
                .andExpect(jsonPath("$.props.userRecommendation", hasSize(0)))
                .andExpect(jsonPath("$.props.resumeComments", hasSize(0)))
                .andExpect(jsonPath("$.props.careerTracks", hasSize(0)))

                .andExpect(jsonPath("$.props.totalAnswers").value(0))
                .andExpect(jsonPath("$.props.totalComments").value(0))
                .andExpect(jsonPath("$.props.totalLikes").value(0));
    }

    @Test
    void testUserPageNotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(get("/users/" + nonExistentId)
                        .header("X-Inertia", "true"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.props.status").value(404))
                .andExpect(jsonPath("$.props.userId").value(nonExistentId));
    }
}
