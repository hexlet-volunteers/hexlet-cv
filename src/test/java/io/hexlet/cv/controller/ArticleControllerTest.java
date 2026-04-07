package io.hexlet.cv.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.admin.marketing.Article;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.ArticleRepository;
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
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "admin@example.com";

    @AfterEach
    public void cleanUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        articleRepository.deleteAll();
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

    private Article createArticle(String title, boolean isPublished) {
        var article = Article.builder()
                .title(title)
                .content("Test article content for testing purposes")
                .author("Test Author")
                .readingTime(5)
                .isPublished(isPublished)
                .showOnHomepage(true)
                .homeComponentId("877")
                .displayOrder(1)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return articleRepository.save(article);
    }

    private String generateToken(User user) {
        return jwtUtils.generateAccessToken(user.getEmail());
    }

    @Test
    public void testGetArticlesSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        createArticle("Test article", true);

        mockMvc.perform(get("/admin/marketing/articles")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("articles"))
                .andExpect(jsonPath("$.props.articles").isArray())
                .andExpect(jsonPath("$.props.articles[0].title").value("Test article"));
    }

    @Test
    public void testGetCreateForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/articles/create")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("articles"));
    }

    @Test
    public void testGetEditForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Test Article for Edit", true);

        mockMvc.perform(get("/admin/marketing/articles/{id}/edit", testArticle.getId())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("articles"))
                .andExpect(jsonPath("$.props.article.id").value(testArticle.getId()));
    }


    @Test
    public void testCreateArticle() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String articleJson = """
            {
                "title": "New Article",
                "content": "New content",
                "author": "Test Author",
                "readingTime": 3
            }
            """;

        mockMvc.perform(post("/admin/marketing/articles")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/articles"));
    }

    @Test
    public void testUpdateArticle() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Update Test", true);

        String articleJson = """
            {
                "title": "Updated Article",
                "content": "Updated content",
                "author": "Updated Author"
            }
            """;

        mockMvc.perform(put("/admin/marketing/articles/{id}", testArticle.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/articles"));
    }


    @Test
    public void testDeleteArticle() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Delete Test", true);
        var articleId = testArticle.getId();

        mockMvc.perform(delete("/admin/marketing/articles/{id}", testArticle.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/articles"));
    }

    @Test
    public void testToggleArticleHomepage() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Homepage Test", true);
        boolean initialHomepage = testArticle.getShowOnHomepage();

        mockMvc.perform(post("/admin/marketing/articles/{id}/toggle-homepage", testArticle.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/home-components"));

        var toogledArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertEquals(!initialHomepage, toogledArticle.getShowOnHomepage());
    }

    @Test
    public void testTogglePublish() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Publish Test", true);
        boolean initialPublished = testArticle.getIsPublished();

        mockMvc.perform(post("/admin/marketing/articles/{id}/toggle-publish", testArticle.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/articles"));
        var toggledArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertEquals(!initialPublished, toggledArticle.getIsPublished());
    }

    @Test
    public void testUpdateArticleDisplayOrder() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testArticle = createArticle("Display Order Test", true);

        String json = "{\"display_order\": 5}";

        mockMvc.perform(put("/admin/marketing/articles/{id}/display-order", testArticle.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        var updatedArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertEquals(5, updatedArticle.getDisplayOrder());
    }

    @Test
    public void testAccessAsNonAdmin() throws Exception {
        var candidate = new User();
        candidate.setEmail("candidate@example.com");
        candidate.setEncryptedPassword(encoder.encode("password"));
        candidate.setRole(RoleType.CANDIDATE);
        userRepository.save(candidate);

        String candidateToken = jwtUtils.generateAccessToken("candidate@example.com");

        mockMvc.perform(get("/admin/marketing/articles")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isForbidden());
    }
}
