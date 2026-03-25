package io.hexlet.cv.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testInertiaLogoutUserCookies() throws Exception {

        mockMvc.perform(post("/users/sign_out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .header("X-Inertia", "true")
                        .header("Referer", "/users/sign_out"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,
                        Matchers.hasItem(Matchers.allOf(
                                Matchers.containsString("access_token"),
                                Matchers.containsString("Max-Age=0")
                        ))))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,
                        Matchers.hasItem(Matchers.allOf(
                                Matchers.containsString("refresh_token"),
                                Matchers.containsString("Max-Age=0")
                        ))));
    }
}
