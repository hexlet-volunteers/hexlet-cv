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
import io.hexlet.cv.model.admin.marketing.Review;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.ReviewRepository;
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
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "admin@example.com";

    @AfterEach
    public void cleanUp() {
        reviewRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
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

    private Review createReview(String author, boolean isPublished) {
        var review = Review.builder()
                .author(author)
                .content("Test review content for testing purposes")
                .avatarUrl("https://example.com/avatar.jpg")
                .isPublished(isPublished)
                .showOnHomepage(true)
                .displayOrder(1)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return reviewRepository.save(review);
    }

    private String generateToken(User user) {
        return jwtUtils.generateAccessToken(user.getEmail());
    }

    @Test
    public void testGetReviewsSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = jwtUtils.generateAccessToken(ADMIN_EMAIL);
        createReview("Test Author", true);

        createReview("Test Author", true);
        mockMvc.perform(get("/admin/marketing/reviews")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("reviews"))
                .andExpect(jsonPath("$.props.reviews").isArray())
                .andExpect(jsonPath("$.props.reviews[0].author").value("Test Author"));
    }

    @Test
    public void testGetCreateForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/reviews/create")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("reviews"));
    }

    @Test
    public void testGetEditForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Test Author for Edit", true);

        mockMvc.perform(get("/admin/marketing/reviews/{id}/edit", testReview.getId())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("reviews"))
                .andExpect(jsonPath("$.props.review.id").value(testReview.getId()));
    }

    @Test
    public void testCreateReview() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String reviewJson = """
            {
                "author": "New Review Author",
                "content": "New review content",
                "avatarUrl": "https://example.com/new-avatar.jpg",
                "isPublished": false,
                "showOnHomepage": true,
                "displayOrder": 2
            }
            """;

        mockMvc.perform(post("/admin/marketing/reviews")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/reviews"));

        assertEquals(1, reviewRepository.count());
        var savedReview = reviewRepository.findAll().get(0);
        assertEquals("New Review Author", savedReview.getAuthor());
        assertEquals("New review content", savedReview.getContent());
        assertFalse(savedReview.getIsPublished());
    }

    @Test
    public void testUpdateReview() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Original Author", true);
        LocalDateTime originalPublishedAt = testReview.getPublishedAt();



        String reviewJson = """
            {
                "author": "Updated Author",
                "content": "Updated review content",
                "avatarUrl": "https://example.com/updated-avatar.jpg",
                "isPublished": true,
                "showOnHomepage": false,
                "displayOrder": 5
            }
            """;

        mockMvc.perform(put("/admin/marketing/reviews/{id}", testReview.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/reviews"));

        var updatedReview = reviewRepository.findById(testReview.getId()).orElseThrow();
        assertEquals("Updated Author", updatedReview.getAuthor());
        assertEquals("Updated review content", updatedReview.getContent());
        assertEquals("https://example.com/updated-avatar.jpg", updatedReview.getAvatarUrl());
        assertFalse(updatedReview.getShowOnHomepage());
        assertTrue(updatedReview.getIsPublished());
        assertEquals(5, updatedReview.getDisplayOrder());
    }

    @Test
    public void testDeleteReview() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Test Author", true);
        var reviewId = testReview.getId();

        mockMvc.perform(delete("/admin/marketing/reviews/{id}", testReview.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/reviews"));

        assertFalse(reviewRepository.existsById(reviewId));
    }


    @Test
    public void testTogglePublishReview() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Test Author", true);
        boolean initialPublished = testReview.getIsPublished();

        mockMvc.perform(post("/admin/marketing/reviews/{id}/toggle-publish", testReview.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/reviews"));

        Review updatedReview = reviewRepository.findById(testReview.getId()).orElseThrow();
        assertEquals(!initialPublished, updatedReview.getIsPublished());
    }

    @Test
    public void testToggleHomepageReview() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Test Author", true);

        boolean initialHomepage = testReview.getShowOnHomepage();

        mockMvc.perform(post("/admin/marketing/reviews/{id}/toggle-homepage", testReview.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/home-components"));

        var toggleReview = reviewRepository.findById(testReview.getId()).orElseThrow();
        assertEquals(!initialHomepage, toggleReview.getShowOnHomepage());
    }


    @Test
    public void testUpdateReviewDisplayOrder() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testReview = createReview("Test Author", true);

        String json = "{\"display_order\": 3}";

        mockMvc.perform(put("/admin/marketing/reviews/{id}/display-order", testReview.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        var updatedReview = reviewRepository.findById(testReview.getId()).orElseThrow();
        assertEquals(3, updatedReview.getDisplayOrder());
    }

    @Test
    public void testGetHomeComponentsSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        createReview("Homepage Review", true).setShowOnHomepage(true);

        mockMvc.perform(get("/admin/marketing/home-components")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("home-components"))
                .andExpect(jsonPath("$.props.reviews").exists())
                .andExpect(jsonPath("$.props.reviews[0].author").value("Homepage Review"));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/admin/marketing/reviews")
                        .header("X-Inertia", "true"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
