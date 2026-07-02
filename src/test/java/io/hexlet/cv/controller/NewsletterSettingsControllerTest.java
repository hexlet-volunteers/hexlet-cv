package io.hexlet.cv.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.dto.NewsletterSettingsDTO;
import io.hexlet.cv.model.NewsletterSettings;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.NewsletterSettingsRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.utils.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NewsletterSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsletterSettingsRepository newsletterSettingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = modelGenerator.createUserWithSettings();
        testUser.setEmail("test@example.com");
        testUser.setLocale("ru");
        userRepository.save(testUser);

        if (testUser.getNewsletterSettings() != null) {
            newsletterSettingsRepository.save(testUser.getNewsletterSettings());
        }
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetEditPage() throws Exception {
        mockMvc.perform(get("/account/newsletters/edit")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Account/Newsletters/Edit")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("settings")));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testUpdateSettings() throws Exception {
        NewsletterSettingsDTO updateDto = NewsletterSettingsDTO.builder()
                .newCourses(false)
                .courseUpdates(true)
                .promotions(true)
                .achievements(false)
                .commentsReplies(true)
                .resumeViews(false)
                .vacancyMatches(true)
                .communityNews(true)
                .marketingTips(false)
                .build();

        String requestBody = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(post("/account/newsletters/edit")
                        .with(csrf())
                        .header("X-Inertia", "true")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/account/newsletters/edit"));

        NewsletterSettings savedSettings = newsletterSettingsRepository.findByUserId(testUser.getId()).orElseThrow();
        assertThat(savedSettings.getNewCourses()).isFalse();
        assertThat(savedSettings.getPromotions()).isTrue();
        assertThat(savedSettings.getAchievements()).isFalse();
        assertThat(savedSettings.getCommunityNews()).isTrue();
    }

    @Test
    @WithMockUser(username = "no-settings@example.com")
    void testUserWithoutSettings() throws Exception {

        User userWithoutSettings = User.builder()
                .email("no-settings@example.com")
                .firstName("Test")
                .lastName("User")
                .encryptedPassword(passwordEncoder.encode("password"))
                .role(RoleType.CANDIDATE)
                .state("active")
                .locale("ru")
                .newsletterSettings(null)
                .build();

        userRepository.save(userWithoutSettings);

        mockMvc.perform(get("/account/newsletters/edit")
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Account/Newsletters/Edit")));

        NewsletterSettings settings = newsletterSettingsRepository.findByUserId(userWithoutSettings.getId())
                .orElseThrow(() -> new AssertionError("Settings should have been created"));

        assertThat(settings).isNotNull();
        assertThat(settings.getNewCourses()).isTrue();
        assertThat(settings.getCourseUpdates()).isTrue();
        assertThat(settings.getPromotions()).isFalse();
        assertThat(settings.getAchievements()).isTrue();
        assertThat(settings.getCommentsReplies()).isTrue();
        assertThat(settings.getResumeViews()).isTrue();
        assertThat(settings.getVacancyMatches()).isTrue();
        assertThat(settings.getCommunityNews()).isFalse();
        assertThat(settings.getMarketingTips()).isFalse();
    }
}
