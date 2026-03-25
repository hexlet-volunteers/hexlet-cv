package io.hexlet.cv.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.model.knowledge.KnowledgeArticle;
import io.hexlet.cv.model.knowledge.KnowledgeInterview;
import io.hexlet.cv.repository.KnowledgeArticleRepository;
import io.hexlet.cv.repository.KnowledgeInterviewRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.KnowledgeService;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
public class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private KnowledgeArticleRepository knowledgeArticleRepository;

    @Autowired
    private KnowledgeInterviewRepository knowledgeInterviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    private String candidateToken;
    private User candidateUser;

    private static final String CANDIDATE_EMAIL = "candidate@example.com";

    @BeforeEach
    void setUp() {
        knowledgeArticleRepository.deleteAll();
        knowledgeInterviewRepository.deleteAll();
        userRepository.deleteAll();

        candidateUser = User.builder()
                .email(CANDIDATE_EMAIL)
                .encryptedPassword(passwordEncoder.encode("password"))
                .role(RoleType.CANDIDATE)
                .build();
        userRepository.save(candidateUser);
        candidateToken = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private KnowledgeArticle createTestArticle(String title, String category, boolean isPublished) {
        var article = KnowledgeArticle.builder()
                .title(title)
                .description("Description for " + title)
                .category(category)
                .readingTime("5 мин")
                .imageUrl("/images/test.jpg")
                .isPublished(isPublished)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return knowledgeArticleRepository.save(article);
    }

    private KnowledgeInterview createTestInterview(String title, String category, boolean isPublished) {
        var interview = KnowledgeInterview.builder()
                .title(title)
                .description("Interview description for " + title)
                .category(category)
                .duration("12 мин")
                .questionsCount(10)
                .imageUrl("/images/interview.jpg")
                .isPublished(isPublished)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return knowledgeInterviewRepository.save(interview);
    }

    @Test
    @SneakyThrows
    public void getKnowledgeHomeReturnsRecentArticlesAndInterviews() {
        createTestArticle("Published Article 1", "SQL", true);
        createTestArticle("Published Article 2", "Java", true);
        createTestArticle("Unpublished Article", "Python", false);

        createTestInterview("Published Interview 1", "SQL", true);
        createTestInterview("Published Interview 2", "Java", true);
        createTestInterview("Unpublished Interview", "Python", false);

        String response = mockMvc.perform(get("/account/knowledge")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .contains("Published Article 1")
                .contains("Published Article 2")
                .contains("Published Interview 1")
                .contains("Published Interview 2")
                .doesNotContain("Unpublished Article")
                .doesNotContain("Unpublished Interview");
        assertThat(response).contains("\"activeMainSection\":\"account\"");
        assertThat(response).contains("\"activeSubSection\":\"knowledge\"");
    }

    @Test
    @SneakyThrows
    void getArticlesReturnsPaginatedPublishedArticles() {
        for (int i = 1; i <= 15; i++) {
            createTestArticle("Article " + i, "SQL", true);
        }

        mockMvc.perform(get("/account/knowledge/articles")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.pagination.totalElements").value(15))
                .andExpect(jsonPath("$.props.pagination.totalPages").value(2))
                .andExpect(jsonPath("$.props.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.props.selectedCategory").doesNotExist())
                .andExpect(jsonPath("$.props.activeMainSection").value("account"))
                .andExpect(jsonPath("$.props.activeSubSection").value("knowledge-articles"));
    }

    @Test
    @SneakyThrows
    void getArticleByIdReturnsPublishedArticle() {
        KnowledgeArticle article = createTestArticle("Test Article", "SQL", true);
        createTestArticle("Another Article", "Java", true);

        String response = mockMvc.perform(get("/account/knowledge/articles/" + article.getId())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .contains("\"title\":\"Test Article\"")
                .contains("\"category\":\"SQL\"")
                .contains("\"activeMainSection\":\"account\"")
                .contains("\"activeSubSection\":\"knowledge-articles\"")
                .doesNotContain("Another Article");
    }

    @Test
    @SneakyThrows
    void getArticleByIdUnpublishedArticleReturnsNotFound() {
        var unpublishedArticle = createTestArticle("Unpublished Article", "SQL", false);

        mockMvc.perform(get("/account/knowledge/articles/" + unpublishedArticle.getId())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther());
    }

    @Test
    @SneakyThrows
    void getAllInterviewsWithCategoryReturnsFilteredInterviews() {
        createTestInterview("Java Interview 1", "Java", true);
        createTestInterview("Java Interview 2", "Java", true);
        createTestInterview("SQL Interview", "SQL", true);
        createTestInterview("Python Interview", "Python", true);

        String response = mockMvc.perform(get("/account/knowledge/interviews")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("category", "Java"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .contains("Java Interview 1")
                .contains("Java Interview 2")
                .doesNotContain("SQL Interview")
                .doesNotContain("Python Interview")
                .contains("\"selectedCategory\":\"Java\"");
    }

    @Test
    @SneakyThrows
    void getAllInterviewsReturnsPaginatedPublishedInterviews() {
        for (int i = 1; i <= 12; i++) {
            createTestInterview("Interview " + i, "Java", true);
        }

        mockMvc.perform(get("/account/knowledge/interviews")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.pagination.totalElements").value(12))
                .andExpect(jsonPath("$.props.pagination.totalPages").value(3))
                .andExpect(jsonPath("$.props.pagination.pageSize").value(5))
                .andExpect(jsonPath("$.props.selectedCategory").doesNotExist())
                .andExpect(jsonPath("$.props.activeMainSection").value("account"))
                .andExpect(jsonPath("$.props.activeSubSection").value("knowledge-interviews"));
    }

    @Test
    @SneakyThrows
    void getInterviewByIdReturnsPublishedInterview() {
        KnowledgeInterview interview = createTestInterview("Test Interview", "Java", true);
        createTestInterview("Another Interview", "SQL", true);

        String response = mockMvc.perform(get("/account/knowledge/interviews/" + interview.getId())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .contains("\"title\":\"Test Interview\"")
                .contains("\"category\":\"Java\"")
                .contains("\"questionsCount\":10")
                .contains("\"activeMainSection\":\"account\"")
                .contains("\"activeSubSection\":\"knowledge-interviews\"")
                .doesNotContain("Another Interview");
    }

    @Test
    @SneakyThrows
    void getInterviewByIdUnpublishedInterviewReturnsNotFound() {
        KnowledgeInterview unpublishedInterview = createTestInterview("Unpublished Interview", "Java", false);

        mockMvc.perform(get("/account/knowledge/interviews/" + unpublishedInterview.getId())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther());
    }

    @Test
    @SneakyThrows
    void defaultRedirectRedirectsToKnowledgeHome() {
        mockMvc.perform(get("/account/knowledge/")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/knowledge"));
    }

    @Test
    @SneakyThrows
    void getArticlesWithoutAuthenticationReturnsUnauthorized() {
        mockMvc.perform(get("/account/knowledge/articles")
                        .header("X-Inertia", "true"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void getKnowledgeHomeWithoutInertiaHeaderReturnsHtml() {
        createTestArticle("Test Article", "SQL", true);

        mockMvc.perform(get("/account/knowledge")
                        .cookie(new Cookie("access_token", candidateToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    @SneakyThrows
    void getArticlesWithNonExistingCategoryReturnsEmptyList() {
        createTestArticle("SQL Article", "SQL", true);
        createTestArticle("Java Article", "Java", true);

        String response = mockMvc.perform(get("/account/knowledge/articles")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("category", "NonExisting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.selectedCategory").value("NonExisting"))
                .andExpect(jsonPath("$.props.articles").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .doesNotContain("SQL Article")
                .doesNotContain("Java Article");
    }

    @Test
    @SneakyThrows
    void getRecentItemsLimitedToTwoEach() {
        for (int i = 1; i <= 5; i++) {
            createTestArticle("Article " + i, "Category " + i, true);
            createTestInterview("Interview " + i, "Category " + i, true);
        }
        String response = mockMvc.perform(get("/account/knowledge")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response)
                .contains("Article 5")
                .contains("Article 4")
                .contains("Interview 5")
                .contains("Interview 4")
                .doesNotContain("Article 3")
                .doesNotContain("Interview 3");
    }

    @Test
    @SneakyThrows
    void getArticlesWithEmptyCategoryReturnsAllArticles() {
        createTestArticle("Article 1", "SQL", true);
        createTestArticle("Article 2", "Java", true);

        mockMvc.perform(get("/account/knowledge/articles")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("category", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.articles.length()").value(2));
    }

    @Test
    @SneakyThrows
    void getArticlesWithWhitespaceCategoryReturnsAllArticles() {
        createTestArticle("Article 1", "SQL", true);
        createTestArticle("Article 2", "Java", true);

        mockMvc.perform(get("/account/knowledge/articles")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true")
                        .param("category", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.articles.length()").value(2));
    }
}
