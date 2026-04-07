package io.hexlet.cv.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.admin.marketing.Story;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.StoryRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class StoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "admin@example.com";

    @AfterEach
    public void cleanUp() {
        storyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        storyRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(String email, RoleType role) {
        var user = User.builder()
                .email(email)
                .encryptedPassword(encoder.encode("password"))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private Story createStory(String title, boolean isPublished) {
        var story = Story.builder()
                .title(title)
                .content("Test story content for testing purposes")
                .imageUrl("https://example.com/story-image.jpg")
                .isPublished(isPublished)
                .showOnHomepage(true)
                .displayOrder(1)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return storyRepository.save(story);
    }

    private String generateToken(User user) {
        return jwtUtils.generateAccessToken(user.getEmail());
    }

    @Test
    public void testGetStoriesSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        createStory("Test Story", true);

        mockMvc.perform(get("/admin/marketing/stories")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("stories"))
                .andExpect(jsonPath("$.props.stories").isArray())
                .andExpect(jsonPath("$.props.stories[0].title").value("Test Story"));
    }

    @Test
    public void testGetCreateForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/stories/create")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("stories"));
    }

    @Test
    public void testGetEditForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Edit Test Story", true);

        mockMvc.perform(get("/admin/marketing/stories/{id}/edit", testStory.getId())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("stories"))
                .andExpect(jsonPath("$.props.story.id").value(testStory.getId()))
                .andExpect(jsonPath("$.props.story.title").value("Edit Test Story"));
    }

    @Test
    public void testCreateStory() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String storyJson = """
                {
                    "title": "New Story",
                    "content": "New story content",
                    "imageUrl": "https://example.com/image.jpg",
                    "isPublished": false,
                    "showOnHomepage": true,
                    "displayOrder": 2
                }
                """;

        mockMvc.perform(post("/admin/marketing/stories")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storyJson))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/stories"));

        assertEquals(1, storyRepository.count());
        var savedStory = storyRepository.findAll().get(0);
        assertEquals("New Story", savedStory.getTitle());
        assertEquals("New story content", savedStory.getContent());
        assertEquals("https://example.com/image.jpg", savedStory.getImageUrl());
        assertFalse(savedStory.getIsPublished());
        assertTrue(savedStory.getShowOnHomepage());
        assertEquals(2, savedStory.getDisplayOrder());
    }

    @Test
    public void testUpdateStory() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Original Story", true);

        String storyJson = """
            {
                "title": "Updated Story Title",
                "content": "Updated story content",
                "imageUrl": "https://example.com/updated-image.jpg",
                "isPublished": true,
                "showOnHomepage": false,
                "displayOrder": 5
            }
            """;

        mockMvc.perform(put("/admin/marketing/stories/{id}", testStory.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storyJson))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/stories"));

        var updatedStory = storyRepository.findById(testStory.getId()).orElseThrow();
        assertEquals("Updated Story Title", updatedStory.getTitle());
        assertEquals("Updated story content", updatedStory.getContent());
        assertEquals("https://example.com/updated-image.jpg", updatedStory.getImageUrl());
        assertTrue(updatedStory.getIsPublished());
        assertFalse(updatedStory.getShowOnHomepage());
        assertEquals(5, updatedStory.getDisplayOrder());
    }

    @Test
    public void testDeleteStory() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Delete Test Story", true);
        var storyId = testStory.getId();

        mockMvc.perform(delete("/admin/marketing/stories/{id}", testStory.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/stories"));

        assertFalse(storyRepository.existsById(storyId));
    }

    @Test
    public void testTogglePublishStory() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Delete Test Story", true);
        boolean initialPublishStatus = testStory.getIsPublished();

        mockMvc.perform(post("/admin/marketing/stories/{id}/toggle-publish", testStory.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/stories"));

        var toggledStory = storyRepository.findById(testStory.getId()).orElseThrow();
        assertEquals(!initialPublishStatus, toggledStory.getIsPublished());
    }

    @Test
    public void testToggleStoryHomepage() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Story for Homepage", true);
        boolean initialHomepageStatus = testStory.getShowOnHomepage();

        mockMvc.perform(post("/admin/marketing/stories/{id}/toggle-homepage", testStory.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/home-components"));

        var toggledStory = storyRepository.findById(testStory.getId()).orElseThrow();
        assertEquals(!initialHomepageStatus, toggledStory.getShowOnHomepage());
    }

    @Test
    public void testUpdateStoryDisplayOrder() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testStory = createStory("Story for Display Order", true);

        String json = "{\"display_order\": 5}";

        mockMvc.perform(put("/admin/marketing/stories/{id}/display-order", testStory.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        var updatedStory = storyRepository.findById(testStory.getId()).orElseThrow();
        assertEquals(5, updatedStory.getDisplayOrder());
    }

    @Test
    public void testGetHomeComponentsSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/home-components")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("home-components"))
                .andExpect(jsonPath("$.props.stories").exists());
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/admin/marketing/stories")
                        .header("X-Inertia", "true"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
