package io.hexlet.cv.controller.admin.marketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.marketing.*;
import io.hexlet.cv.model.enums.TeamMemberType;
import io.hexlet.cv.model.enums.TeamPosition;
import io.hexlet.cv.service.*;
import io.hexlet.cv.util.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMarketingController.class)
public class AdminMarketingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ControllerUtils controllerUtils;

    @MockitoBean
    private Inertia inertia;

    @MockitoBean
    public EnumService enumService;

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

    // ─── helpers ────────────────────────────────────────────────────────────

    private static final String ADMIN = "admin";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String REDIRECT_HEADER = "Location";

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

    // ════════════════════════════════════════════════════════════════════════
    // INDEX
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class IndexTests {

        @ParameterizedTest
        @CsvSource({
                "articles, Admin/Marketing/Articles/Index, articles",
                "stories,  Admin/Marketing/Stories/Index,  stories",
                "reviews,  Admin/Marketing/Reviews/Index,  reviews",
                "team,     Admin/Marketing/Team/Index,     team",
                "pricing,  Admin/Marketing/Pricing/Index,  pricing"
        })
        void shouldRenderIndexPageForSection(String section, String componentName, String propKey) throws Exception {
            mockPageForSection(section);

            mockMvc.perform(get("/admin/marketing/" + section)
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isOk());

            verifyServiceCalledForSection(section);
            verify(controllerUtils).createMarketingProps(section);
            verify(controllerUtils).createPaginationMap(any(Page.class), any(Pageable.class));

            ArgumentCaptor<Map<String, Object>> propsCaptor = ArgumentCaptor.forClass(Map.class);
            verify(inertia).render(eq(componentName), propsCaptor.capture());
            assertThat(propsCaptor.getValue()).containsKeys(propKey, "pagination");
        }

        @Test
        void shouldReturn401ForUnauthenticatedIndexRequest() throws Exception {
            mockMvc.perform(get("/admin/marketing/articles"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturn403ForNonAdminIndexRequest() throws Exception {
            mockPageForSection("articles");
            mockMvc.perform(get("/admin/marketing/articles")
                            .with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        private void mockPageForSection(String section) {
            switch (section) {
                case "articles" -> when(articleService.getAllArticles(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
                case "stories"  -> when(storyService.getAllStories(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
                case "reviews"  -> when(reviewService.getAllReviews(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
                case "team"     -> when(teamService.getAllTeamMembers(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
                case "pricing"  -> when(pricingPlanService.getAllPricing(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
            }
        }

        private void verifyServiceCalledForSection(String section) {
            switch (section) {
                case "articles" -> verify(articleService).getAllArticles(any(Pageable.class));
                case "stories"  -> verify(storyService).getAllStories(any(Pageable.class));
                case "reviews"  -> verify(reviewService).getAllReviews(any(Pageable.class));
                case "team"     -> verify(teamService).getAllTeamMembers(any(Pageable.class));
                case "pricing"  -> verify(pricingPlanService).getAllPricing(any(Pageable.class));
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CREATE FORM (GET)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class CreateFormTests {

        @ParameterizedTest
        @CsvSource({
                "articles, Admin/Marketing/Articles/Create",
                "stories,  Admin/Marketing/Stories/Create",
                "reviews,  Admin/Marketing/Reviews/Create",
                "team,     Admin/Marketing/Team/Create",
                "pricing,  Admin/Marketing/Pricing/Create"
        })
        void shouldRenderCreatePageForSection(String section, String componentName) throws Exception {
            mockMvc.perform(get("/admin/marketing/" + section + "/create")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isOk());

            verify(controllerUtils).createMarketingProps(section);
            verify(inertia).render(eq(componentName), anyMap());
            if (section.equals("team")) verify(enumService).getTeamEnums();
        }

        @Test
        void shouldReturn401ForUnauthenticatedCreateFormRequest() throws Exception {
            mockMvc.perform(get("/admin/marketing/articles/create"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // EDIT FORM (GET)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class EditFormTests {

        @ParameterizedTest
        @CsvSource({
                "articles, Admin/Marketing/Articles/Edit",
                "stories,  Admin/Marketing/Stories/Edit",
                "reviews,  Admin/Marketing/Reviews/Edit",
                "team,     Admin/Marketing/Team/Edit",
                "pricing,  Admin/Marketing/Pricing/Edit"
        })
        void shouldRenderEditPageForSection(String section, String componentName) throws Exception {
            mockEntityForSection(section, 1L);

            mockMvc.perform(get("/admin/marketing/" + section + "/1/edit")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isOk());

            verify(inertia).render(eq(componentName), anyMap());
        }

        @Test
        void shouldReturn401ForUnauthenticatedEditFormRequest() throws Exception {
            mockMvc.perform(get("/admin/marketing/articles/1/edit"))
                    .andExpect(status().isUnauthorized());
        }

        private void mockEntityForSection(String section, Long id) {
            switch (section) {
                case "articles" -> when(articleService.getArticleById(id)).thenReturn(null);
                case "stories"  -> when(storyService.getStoryById(id)).thenReturn(null);
                case "reviews"  -> when(reviewService.getReviewById(id)).thenReturn(null);
                case "team"     -> when(teamService.getTeamMemberById(id)).thenReturn(null);
                case "pricing"  -> when(pricingPlanService.getPricingById(id)).thenReturn(null);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CREATE (POST)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class CreateTests {

        // ── Article ──────────────────────────────────────────────────────────

        @Test
        void shouldCreateArticleAndRedirect() throws Exception {
            var dto = new ArticleCreateDto();
            dto.setTitle("test title");
            dto.setAuthor("test author");
            dto.setContent("test content");

            mockMvc.perform(post("/admin/marketing/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/articles"));

            ArgumentCaptor<ArticleCreateDto> captor = ArgumentCaptor.forClass(ArticleCreateDto.class);
            verify(articleService).createArticle(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("test title");
            assertThat(captor.getValue().getAuthor()).isEqualTo("test author");
            assertThat(captor.getValue().getContent()).isEqualTo("test content");
        }

        @Test
        void shouldRejectArticleWithBlankTitle() throws Exception {
            var dto = new ArticleCreateDto();
            dto.setTitle("");
            dto.setAuthor("author");
            dto.setContent("content");

            mockMvc.perform(post("/admin/marketing/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        void shouldRejectArticleWithBlankContent() throws Exception {
            var dto = new ArticleCreateDto();
            dto.setTitle("title");
            dto.setAuthor("author");
            dto.setContent("");

            mockMvc.perform(post("/admin/marketing/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.content").exists());
        }

        @Test
        void shouldReturn401WhenCreatingArticleWithoutAuth() throws Exception {
            var dto = new ArticleCreateDto();
            dto.setTitle("title");
            dto.setAuthor("author");
            dto.setContent("content");

            mockMvc.perform(post("/admin/marketing/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturn403WhenCreatingArticleAsNonAdmin() throws Exception {
            var dto = new ArticleCreateDto();
            dto.setTitle("title");
            dto.setAuthor("author");
            dto.setContent("content");

            mockMvc.perform(post("/admin/marketing/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        // ── Story ────────────────────────────────────────────────────────────

        @Test
        void shouldCreateStoryAndRedirect() throws Exception {
            var dto = new StoryCreateDto();
            dto.setTitle("story title");
            dto.setContent("story content");
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/stories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/stories"));

            ArgumentCaptor<StoryCreateDto> captor = ArgumentCaptor.forClass(StoryCreateDto.class);
            verify(storyService).createStory(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("story title");
            assertThat(captor.getValue().getContent()).isEqualTo("story content");
        }

        @Test
        void shouldRejectStoryWithBlankTitle() throws Exception {
            var dto = new StoryCreateDto();
            dto.setTitle("");
            dto.setContent("content");
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/stories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        void shouldRejectStoryWithMissingShowOnHomepage() throws Exception {
            var dto = new StoryCreateDto();
            dto.setTitle("title");
            dto.setContent("content");
            // showOnHomepage not set → null → @NotNull violation

            mockMvc.perform(post("/admin/marketing/stories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.showOnHomepage").exists());
        }

        @Test
        void shouldRejectStoryWithInvalidImageUrl() throws Exception {
            var dto = new StoryCreateDto();
            dto.setTitle("title");
            dto.setContent("content");
            dto.setShowOnHomepage(false);
            dto.setImageUrl("not-a-url");

            mockMvc.perform(post("/admin/marketing/stories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.imageUrl").exists());
        }

        // ── Review ───────────────────────────────────────────────────────────

        @Test
        void shouldCreateReviewAndRedirect() throws Exception {
            var dto = new ReviewCreateDto();
            dto.setAuthor("reviewer");
            dto.setContent("review content");
            dto.setShowOnHomepage(true);

            mockMvc.perform(post("/admin/marketing/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/reviews"));

            ArgumentCaptor<ReviewCreateDto> captor = ArgumentCaptor.forClass(ReviewCreateDto.class);
            verify(reviewService).createReview(captor.capture());
            assertThat(captor.getValue().getAuthor()).isEqualTo("reviewer");
            assertThat(captor.getValue().getContent()).isEqualTo("review content");
        }

        @Test
        void shouldRejectReviewWithBlankAuthor() throws Exception {
            var dto = new ReviewCreateDto();
            dto.setAuthor("");
            dto.setContent("content");
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.author").exists());
        }

        @Test
        void shouldRejectReviewWithInvalidAvatarUrl() throws Exception {
            var dto = new ReviewCreateDto();
            dto.setAuthor("author");
            dto.setContent("content");
            dto.setShowOnHomepage(false);
            dto.setAvatarUrl("bad-url");

            mockMvc.perform(post("/admin/marketing/reviews")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.avatarUrl").exists());
        }

        // ── Team ─────────────────────────────────────────────────────────────

        @Test
        void shouldCreateTeamMemberAndRedirect() throws Exception {
            var dto = new TeamCreateDto();
            dto.setFirstName("John");
            dto.setLastName("Doe");
            dto.setPosition(TeamPosition.MARKETING);
            dto.setMemberType(TeamMemberType.MENTOR);
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/team")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/team"));

            ArgumentCaptor<TeamCreateDto> captor = ArgumentCaptor.forClass(TeamCreateDto.class);
            verify(teamService).createTeamMember(captor.capture());
            assertThat(captor.getValue().getFirstName()).isEqualTo("John");
            assertThat(captor.getValue().getLastName()).isEqualTo("Doe");
        }

        @Test
        void shouldRejectTeamMemberWithBlankFirstName() throws Exception {
            var dto = new TeamCreateDto();
            dto.setFirstName("");
            dto.setLastName("Doe");
            dto.setPosition(TeamPosition.MARKETING);
            dto.setMemberType(TeamMemberType.MENTOR);
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/team")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.firstName").exists());
        }

        @Test
        void shouldRejectTeamMemberWithMissingPosition() throws Exception {
            var dto = new TeamCreateDto();
            dto.setFirstName("John");
            dto.setLastName("Doe");
            // position not set
            dto.setMemberType(TeamMemberType.MENTOR);
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/team")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.position").exists());
        }

        @Test
        void shouldRejectTeamMemberWithMissingMemberType() throws Exception {
            var dto = new TeamCreateDto();
            dto.setFirstName("John");
            dto.setLastName("Doe");
            dto.setPosition(TeamPosition.MARKETING);
            // memberType not set
            dto.setShowOnHomepage(false);

            mockMvc.perform(post("/admin/marketing/team")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.memberType").exists());
        }

        // ── Pricing ──────────────────────────────────────────────────────────

        @Test
        void shouldCreatePricingAndRedirect() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("Basic");
            dto.setOriginalPrice(99.0);
            dto.setDiscountPercent(10.0);
            dto.setDescription("Basic plan description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/pricing"));

            ArgumentCaptor<PricingCreateDto> captor = ArgumentCaptor.forClass(PricingCreateDto.class);
            verify(pricingPlanService).createPricing(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Basic");
            assertThat(captor.getValue().getOriginalPrice()).isEqualTo(99.0);
        }

        @Test
        void shouldRejectPricingWithBlankName() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("");
            dto.setOriginalPrice(99.0);
            dto.setDiscountPercent(0.0);
            dto.setDescription("description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.name").exists());
        }

        @Test
        void shouldRejectPricingWithZeroPrice() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("Plan");
            dto.setOriginalPrice(0.0); // must be > 0
            dto.setDiscountPercent(0.0);
            dto.setDescription("description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.originalPrice").exists());
        }

        @Test
        void shouldRejectPricingWithDiscountOver100() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("Plan");
            dto.setOriginalPrice(99.0);
            dto.setDiscountPercent(150.0); // exceeds 100
            dto.setDescription("description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.discountPercent").exists());
        }

        @Test
        void shouldRejectPricingWithNegativeDiscount() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("Plan");
            dto.setOriginalPrice(99.0);
            dto.setDiscountPercent(-5.0);
            dto.setDescription("description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.discountPercent").exists());
        }

        @Test
        void shouldRejectPricingWithNameExceeding100Chars() throws Exception {
            var dto = new PricingCreateDto();
            dto.setName("A".repeat(101));
            dto.setOriginalPrice(99.0);
            dto.setDiscountPercent(0.0);
            dto.setDescription("description");

            mockMvc.perform(post("/admin/marketing/pricing")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.name").exists());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UPDATE (PUT)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateArticleAndRedirect() throws Exception {
            var dto = new ArticleUpdateDto();
            dto.setTitle(JsonNullable.of("updated title"));
            dto.setContent(JsonNullable.of("updated content"));

            mockMvc.perform(put("/admin/marketing/articles/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/articles"));

            verify(articleService).updateArticle(eq(1L), any(ArticleUpdateDto.class));
        }

        @Test
        void shouldUpdateStoryAndRedirect() throws Exception {
            var dto = new StoryUpdateDto();
            dto.setTitle(JsonNullable.of("updated story"));

            mockMvc.perform(put("/admin/marketing/stories/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/stories"));

            verify(storyService).updateStory(eq(2L), any(StoryUpdateDto.class));
        }

        @Test
        void shouldUpdateReviewAndRedirect() throws Exception {
            var dto = new ReviewUpdateDto();
            dto.setContent(JsonNullable.of("updated review"));

            mockMvc.perform(put("/admin/marketing/reviews/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/reviews"));

            verify(reviewService).updateReview(eq(3L), any(ReviewUpdateDto.class));
        }

        @Test
        void shouldUpdateTeamMemberAndRedirect() throws Exception {
            var dto = new TeamUpdateDto();
            dto.setFirstName(JsonNullable.of("Jane"));

            mockMvc.perform(put("/admin/marketing/team/4")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/team"));

            verify(teamService).updateTeamMember(eq(4L), any(TeamUpdateDto.class));
        }

        @Test
        void shouldUpdatePricingAndRedirect() throws Exception {
            var dto = new PricingUpdateDto();
            dto.setName(JsonNullable.of("Pro"));
            dto.setOriginalPrice(JsonNullable.of(199.0));

            mockMvc.perform(put("/admin/marketing/pricing/5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/pricing"));

            verify(pricingPlanService).updatePricing(eq(5L), any(PricingUpdateDto.class));
        }

        @Test
        void shouldRejectPricingUpdateWithInvalidDiscount() throws Exception {
            var dto = new PricingUpdateDto();
            dto.setDiscountPercent(JsonNullable.of(150.0)); // exceeds 100

            mockMvc.perform(put("/admin/marketing/pricing/5")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.discountPercent").exists());
        }

        @Test
        void shouldReturn401WhenUpdatingWithoutAuth() throws Exception {
            var dto = new ArticleUpdateDto();
            dto.setTitle(JsonNullable.of("title"));

            mockMvc.perform(put("/admin/marketing/articles/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturn403WhenUpdatingAsNonAdmin() throws Exception {
            var dto = new ArticleUpdateDto();
            dto.setTitle(JsonNullable.of("title"));

            mockMvc.perform(put("/admin/marketing/articles/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                            .with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // DELETE
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class DeleteTests {

        @ParameterizedTest
        @CsvSource({
                "articles,/admin/marketing/articles",
                "stories,/admin/marketing/stories",
                "reviews,/admin/marketing/reviews",
                "team,/admin/marketing/team",
                "pricing,/admin/marketing/pricing"
        })
        void shouldDeleteAndRedirect(String section, String redirectUrl) throws Exception {
            mockMvc.perform(delete("/admin/marketing/" + section + "/1")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, redirectUrl));

            verifyDeleteCalledForSection(section, 1L);
        }

        @Test
        void shouldReturn401WhenDeletingWithoutAuth() throws Exception {
            mockMvc.perform(delete("/admin/marketing/articles/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturn403WhenDeletingAsNonAdmin() throws Exception {
            mockMvc.perform(delete("/admin/marketing/articles/1")
                            .with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        private void verifyDeleteCalledForSection(String section, Long id) {
            switch (section.strip()) {
                case "articles" -> verify(articleService).deleteArticle(id);
                case "stories"  -> verify(storyService).deleteStory(id);
                case "reviews"  -> verify(reviewService).deleteReview(id);
                case "team"     -> verify(teamService).deleteTeamMember(id);
                case "pricing"  -> verify(pricingPlanService).deletePricing(id);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOGGLE PUBLISH / HOMEPAGE
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class ToggleTests {

        @ParameterizedTest
        @CsvSource({
                "articles,/admin/marketing/articles",
                "stories,/admin/marketing/stories",
                "reviews,/admin/marketing/reviews",
                "team,/admin/marketing/team"
        })
        void shouldTogglePublishAndRedirect(String section, String redirectUrl) throws Exception {
            mockMvc.perform(post("/admin/marketing/" + section + "/1/toggle-publish")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, redirectUrl));

            verifyTogglePublishCalledForSection(section, 1L);
        }

        @ParameterizedTest
        @CsvSource({
                "articles",
                "stories",
                "reviews",
                "team"
        })
        void shouldToggleHomepageAndRedirectToHomeComponents(String section) throws Exception {
            mockMvc.perform(post("/admin/marketing/" + section + "/1/toggle-homepage")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(REDIRECT_HEADER, "/admin/marketing/home-components"));

            verifyToggleHomepageCalledForSection(section, 1L);
        }

        @Test
        void shouldReturn401WhenTogglingWithoutAuth() throws Exception {
            mockMvc.perform(post("/admin/marketing/articles/1/toggle-publish"))
                    .andExpect(status().isUnauthorized());
        }

        private void verifyTogglePublishCalledForSection(String section, Long id) {
            switch (section) {
                case "articles" -> verify(articleService).togglePublish(id);
                case "stories"  -> verify(storyService).togglePublish(id);
                case "reviews"  -> verify(reviewService).togglePublish(id);
                case "team"     -> verify(teamService).togglePublish(id);
            }
        }

        private void verifyToggleHomepageCalledForSection(String section, Long id) {
            switch (section) {
                case "articles" -> verify(articleService).toggleArticleHomepageVisibility(id);
                case "stories"  -> verify(storyService).toggleStoryHomepageVisibility(id);
                case "reviews"  -> verify(reviewService).toggleReviewHomepageVisibility(id);
                case "team"     -> verify(teamService).toggleTeamMemberHomepageVisibility(id);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // DISPLAY ORDER (PUT /{section}/{id}/display-order)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class DisplayOrderTests {

        // Валидные запросы доходят до сервиса → нужен verify.
        // Невалидные (-1, null) отклоняются на уровне валидации → сервис не вызывается, verify не нужен.

        @ParameterizedTest
        @CsvSource({
                "articles,3",
                "stories,3",
                "reviews,3",
                "team,3",
                "articles,0"  // граничный случай: 0 допустим по @Min(0)
        })
        void shouldUpdateDisplayOrderAndReturn200(String section, int order) throws Exception {
            var body = Map.of("displayOrder", order);

            mockMvc.perform(put("/admin/marketing/" + section + "/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isOk());

            verifyDisplayOrderCalledForSection(section, 1L, order);
        }

        @Test
        void shouldRejectNegativeDisplayOrder() throws Exception {
            var body = Map.of("displayOrder", -1);

            mockMvc.perform(put("/admin/marketing/articles/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors.displayOrder").exists());
        }

        @Test
        void shouldRejectNullDisplayOrder() throws Exception {
            // Если поле Integer + @NotNull → 422 с errors.displayOrder.
            // Если примитив int → Jackson кидает 400 при десериализации.
            // Уточни статус после проверки DisplayOrderRequest.
            var body = new HashMap<String, Object>();
            body.put("displayOrder", null);

            mockMvc.perform(put("/admin/marketing/articles/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        void shouldReturn401WhenUpdatingDisplayOrderWithoutAuth() throws Exception {
            mockMvc.perform(put("/admin/marketing/articles/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("displayOrder", 1))))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturn403WhenUpdatingDisplayOrderAsNonAdmin() throws Exception {
            mockMvc.perform(put("/admin/marketing/articles/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("displayOrder", 1)))
                            .with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        private void verifyDisplayOrderCalledForSection(String section, Long id, int order) {
            switch (section) {
                case "articles" -> verify(articleService).updateArticleDisplayOrder(id, order);
                case "stories"  -> verify(storyService).updateStoryDisplayOrder(id, order);
                case "reviews"  -> verify(reviewService).updateReviewDisplayOrder(id, order);
                case "team"     -> verify(teamService).updateTeamMemberDisplayOrder(id, order);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UNKNOWN SECTION
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    class UnknownSectionTests {

        // Контроллер бросает ResourceNotFoundException для неизвестных секций.
        // Ожидаемый статус — 404 (предполагаем стандартный маппинг хендлера).
        // Если у вас другой ResponseStatus в ExceptionHandler — скорректируй.

        @Test
        void shouldReturn404ForUnknownIndexSection() throws Exception {
            mockMvc.perform(get("/admin/marketing/unknown")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404ForUnknownCreateFormSection() throws Exception {
            mockMvc.perform(get("/admin/marketing/unknown/create")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404ForUnknownEditFormSection() throws Exception {
            mockMvc.perform(get("/admin/marketing/unknown/1/edit")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404ForUnknownDeleteSection() throws Exception {
            mockMvc.perform(delete("/admin/marketing/unknown/1")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404ForUnknownTogglePublishSection() throws Exception {
            mockMvc.perform(post("/admin/marketing/unknown/1/toggle-publish")
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn404ForUnknownDisplayOrderSection() throws Exception {
            var body = Map.of("displayOrder", 1);

            mockMvc.perform(put("/admin/marketing/unknown/1/display-order")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            .with(user(ADMIN).roles(ROLE_ADMIN)))
                    .andExpect(status().isNotFound());
        }
    }
}