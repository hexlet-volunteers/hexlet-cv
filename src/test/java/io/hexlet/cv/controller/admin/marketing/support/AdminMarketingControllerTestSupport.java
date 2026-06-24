package io.hexlet.cv.controller.admin.marketing.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.component.DataInitializer;
import io.hexlet.cv.service.ArticleService;
import io.hexlet.cv.service.EnumService;
import io.hexlet.cv.service.PricingPlanService;
import io.hexlet.cv.service.ReviewService;
import io.hexlet.cv.service.StoryService;
import io.hexlet.cv.service.TeamService;
import io.hexlet.cv.util.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Общая инфраструктура для тестов {@code AdminMarketingController}:
 * моки сервисов, Inertia, константы секций и helper-методы.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AdminMarketingControllerTestSupport {

    protected static final String ADMIN = "admin";
    protected static final String ROLE_ADMIN = "ADMIN";
    protected static final String REDIRECT_HEADER = "Location";
    protected static final String SECTION_ARTICLES = "articles";
    protected static final String SECTION_STORIES = "stories";
    protected static final String SECTION_REVIEWS = "reviews";
    protected static final String SECTION_TEAM = "team";
    protected static final String SECTION_PRICING = "pricing";
    protected static final String SECTION_HOME_COMPONENTS = "home-components";
    protected static final String MARKETING_BASE_PATH = "/admin/marketing";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ControllerUtils controllerUtils;

    @MockitoBean
    private Inertia inertia;

    @MockitoBean
    private EnumService enumService;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private StoryService storyService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private PricingPlanService pricingPlanService;

    @MockitoBean
    private DataInitializer dataInitializer;

    protected MockMvc mockMvc() {
        return mockMvc;
    }

    protected ObjectMapper objectMapper() {
        return objectMapper;
    }

    protected ControllerUtils controllerUtils() {
        return controllerUtils;
    }

    protected Inertia inertia() {
        return inertia;
    }

    protected EnumService enumService() {
        return enumService;
    }

    protected ArticleService articleService() {
        return articleService;
    }

    protected StoryService storyService() {
        return storyService;
    }

    protected ReviewService reviewService() {
        return reviewService;
    }

    protected TeamService teamService() {
        return teamService;
    }

    protected PricingPlanService pricingPlanService() {
        return pricingPlanService;
    }

    protected static String sectionPath(String section) {
        return MARKETING_BASE_PATH + "/" + section;
    }

    @BeforeEach
    void setUp() {
        when(inertia.redirect(anyString())).thenAnswer(inv -> {
            String url = inv.getArgument(0);
            return ResponseEntity.status(303).header(REDIRECT_HEADER, url).build();
        });
        when(inertia.render(anyString(), anyMap())).thenReturn(ResponseEntity.ok("rendered"));
        when(controllerUtils.createMarketingProps(anyString())).thenReturn(new HashMap<>());
        when(controllerUtils.createPaginationMap(any(), any())).thenReturn(new HashMap<>());
        when(enumService.getTeamEnums()).thenReturn(new HashMap<>());
    }

    protected void mockPageForSection(String section) {
        switch (section) {
            case SECTION_ARTICLES -> when(articleService.getAllArticles(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            case SECTION_STORIES -> when(storyService.getAllStories(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            case SECTION_REVIEWS -> when(reviewService.getAllReviews(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            case SECTION_TEAM -> when(teamService.getAllTeamMembers(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            case SECTION_PRICING -> when(pricingPlanService.getAllPricing(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            default -> { }
        }
    }

    protected void verifyServiceCalledForSection(String section) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).getAllArticles(any(Pageable.class));
            case SECTION_STORIES -> verify(storyService).getAllStories(any(Pageable.class));
            case SECTION_REVIEWS -> verify(reviewService).getAllReviews(any(Pageable.class));
            case SECTION_TEAM -> verify(teamService).getAllTeamMembers(any(Pageable.class));
            case SECTION_PRICING -> verify(pricingPlanService).getAllPricing(any(Pageable.class));
            default -> { }
        }
    }

    protected void mockEntityForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> when(articleService.getArticleById(id)).thenReturn(null);
            case SECTION_STORIES -> when(storyService.getStoryById(id)).thenReturn(null);
            case SECTION_REVIEWS -> when(reviewService.getReviewById(id)).thenReturn(null);
            case SECTION_TEAM -> when(teamService.getTeamMemberById(id)).thenReturn(null);
            case SECTION_PRICING -> when(pricingPlanService.getPricingById(id)).thenReturn(null);
            default -> { }
        }
    }

    protected void verifyDeleteCalledForSection(String section, Long id) {
        switch (section.strip()) {
            case SECTION_ARTICLES -> verify(articleService).deleteArticle(id);
            case SECTION_STORIES -> verify(storyService).deleteStory(id);
            case SECTION_REVIEWS -> verify(reviewService).deleteReview(id);
            case SECTION_TEAM -> verify(teamService).deleteTeamMember(id);
            case SECTION_PRICING -> verify(pricingPlanService).deletePricing(id);
            default -> { }
        }
    }

    protected void verifyTogglePublishCalledForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).togglePublish(id);
            case SECTION_STORIES -> verify(storyService).togglePublish(id);
            case SECTION_REVIEWS -> verify(reviewService).togglePublish(id);
            case SECTION_TEAM -> verify(teamService).togglePublish(id);
            default -> { }
        }
    }

    protected void verifyToggleHomepageCalledForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).toggleArticleHomepageVisibility(id);
            case SECTION_STORIES -> verify(storyService).toggleStoryHomepageVisibility(id);
            case SECTION_REVIEWS -> verify(reviewService).toggleReviewHomepageVisibility(id);
            case SECTION_TEAM -> verify(teamService).toggleTeamMemberHomepageVisibility(id);
            default -> { }
        }
    }

    protected void verifyDisplayOrderCalledForSection(String section, Long id, int order) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).updateArticleDisplayOrder(id, order);
            case SECTION_STORIES -> verify(storyService).updateStoryDisplayOrder(id, order);
            case SECTION_REVIEWS -> verify(reviewService).updateReviewDisplayOrder(id, order);
            case SECTION_TEAM -> verify(teamService).updateTeamMemberDisplayOrder(id, order);
            default -> { }
        }
    }
}
