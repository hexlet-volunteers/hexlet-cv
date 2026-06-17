package io.hexlet.cv.controller.admin.marketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.component.DataInitializer;
import io.hexlet.cv.dto.marketing.*;
import io.hexlet.cv.model.enums.TeamMemberType;
import io.hexlet.cv.model.enums.TeamPosition;
import io.hexlet.cv.service.*;
import io.hexlet.cv.util.ControllerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
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

    @MockitoBean
    private DataInitializer dataInitializer;

    private static final String ADMIN = "admin";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String REDIRECT_HEADER = "Location";
    private static final String SECTION_ARTICLES = "articles";
    private static final String SECTION_STORIES = "stories";
    private static final String SECTION_REVIEWS = "reviews";
    private static final String SECTION_TEAM = "team";
    private static final String SECTION_PRICING = "pricing";
    private static final String SECTION_HOME_COMPONENTS = "home-components";
    private static final String MARKETING_BASE_PATH = "/admin/marketing";

    private static String sectionPath(String section) {
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

    // --- index ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + ", Admin/Marketing/Articles/Index, " + SECTION_ARTICLES,
            SECTION_STORIES + ",  Admin/Marketing/Stories/Index,  " + SECTION_STORIES,
            SECTION_REVIEWS + ",  Admin/Marketing/Reviews/Index,  " + SECTION_REVIEWS,
            SECTION_TEAM + ",     Admin/Marketing/Team/Index,     " + SECTION_TEAM,
            SECTION_PRICING + ",  Admin/Marketing/Pricing/Index,  " + SECTION_PRICING
    })
    void shouldRenderIndexPageForSection(String section, String componentName, String propKey) throws Exception {
        mockPageForSection(section);

        mockMvc.perform(get(sectionPath(section))
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
        mockMvc.perform(get(sectionPath(SECTION_ARTICLES)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403ForNonAdminIndexRequest() throws Exception {
        mockPageForSection(SECTION_ARTICLES);
        mockMvc.perform(get(sectionPath(SECTION_ARTICLES))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // --- create form ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + ", Admin/Marketing/Articles/Create",
            SECTION_STORIES + ",  Admin/Marketing/Stories/Create",
            SECTION_REVIEWS + ",  Admin/Marketing/Reviews/Create",
            SECTION_TEAM + ",     Admin/Marketing/Team/Create",
            SECTION_PRICING + ",  Admin/Marketing/Pricing/Create"
    })
    void shouldRenderCreatePageForSection(String section, String componentName) throws Exception {
        mockMvc.perform(get(sectionPath(section) + "/create")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(controllerUtils).createMarketingProps(section);
        verify(inertia).render(eq(componentName), anyMap());
        if (section.equals(SECTION_TEAM)) verify(enumService).getTeamEnums();
    }

    @Test
    void shouldReturn401ForUnauthenticatedCreateFormRequest() throws Exception {
        mockMvc.perform(get(sectionPath(SECTION_ARTICLES) + "/create"))
                .andExpect(status().isUnauthorized());
    }

    // --- edit form ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + ", Admin/Marketing/Articles/Edit",
            SECTION_STORIES + ",  Admin/Marketing/Stories/Edit",
            SECTION_REVIEWS + ",  Admin/Marketing/Reviews/Edit",
            SECTION_TEAM + ",     Admin/Marketing/Team/Edit",
            SECTION_PRICING + ",  Admin/Marketing/Pricing/Edit"
    })
    void shouldRenderEditPageForSection(String section, String componentName) throws Exception {
        mockEntityForSection(section, 1L);

        mockMvc.perform(get(sectionPath(section) + "/1/edit")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(inertia).render(eq(componentName), anyMap());
    }

    @Test
    void shouldReturn401ForUnauthenticatedEditFormRequest() throws Exception {
        mockMvc.perform(get(sectionPath(SECTION_ARTICLES) + "/1/edit"))
                .andExpect(status().isUnauthorized());
    }

    // --- create ---

    @Test
    void shouldCreateArticleAndRedirect() throws Exception {
        var dto = new ArticleCreateDto();
        dto.setTitle("test title");
        dto.setAuthor("test author");
        dto.setContent("test content");

        mockMvc.perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_ARTICLES)));

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

        mockMvc.perform(post(sectionPath(SECTION_ARTICLES))
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

        mockMvc.perform(post(sectionPath(SECTION_ARTICLES))
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

        mockMvc.perform(post(sectionPath(SECTION_ARTICLES))
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

        mockMvc.perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateStoryAndRedirect() throws Exception {
        var dto = new StoryCreateDto();
        dto.setTitle("story title");
        dto.setContent("story content");
        dto.setShowOnHomepage(false);

        mockMvc.perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_STORIES)));

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

        mockMvc.perform(post(sectionPath(SECTION_STORIES))
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

        mockMvc.perform(post(sectionPath(SECTION_STORIES))
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

        mockMvc.perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.imageUrl").exists());
    }

    @Test
    void shouldCreateReviewAndRedirect() throws Exception {
        var dto = new ReviewCreateDto();
        dto.setAuthor("reviewer");
        dto.setContent("review content");
        dto.setShowOnHomepage(true);

        mockMvc.perform(post(sectionPath(SECTION_REVIEWS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_REVIEWS)));

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

        mockMvc.perform(post(sectionPath(SECTION_REVIEWS))
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

        mockMvc.perform(post(sectionPath(SECTION_REVIEWS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.avatarUrl").exists());
    }

    @Test
    void shouldCreateTeamMemberAndRedirect() throws Exception {
        var dto = new TeamCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPosition(TeamPosition.MARKETING);
        dto.setMemberType(TeamMemberType.MENTOR);
        dto.setShowOnHomepage(false);

        mockMvc.perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_TEAM)));

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

        mockMvc.perform(post(sectionPath(SECTION_TEAM))
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
        dto.setMemberType(TeamMemberType.MENTOR);
        dto.setShowOnHomepage(false);

        mockMvc.perform(post(sectionPath(SECTION_TEAM))
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
        dto.setShowOnHomepage(false);

        mockMvc.perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.memberType").exists());
    }

    @Test
    void shouldCreatePricingAndRedirect() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("Basic");
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(10.0);
        dto.setDescription("Basic plan description");

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_PRICING)));

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

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
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
        dto.setOriginalPrice(0.0);
        dto.setDiscountPercent(0.0);
        dto.setDescription("description");

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
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
        dto.setDiscountPercent(150.0);
        dto.setDescription("description");

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
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

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
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

        mockMvc.perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    // --- update ---

    @Test
    void shouldUpdateArticleAndRedirect() throws Exception {
        var dto = new ArticleUpdateDto();
        dto.setTitle(JsonNullable.of("updated title"));
        dto.setContent(JsonNullable.of("updated content"));

        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_ARTICLES)));

        verify(articleService).updateArticle(eq(1L), any(ArticleUpdateDto.class));
    }

    @Test
    void shouldUpdateStoryAndRedirect() throws Exception {
        var dto = new StoryUpdateDto();
        dto.setTitle(JsonNullable.of("updated story"));

        mockMvc.perform(put(sectionPath(SECTION_STORIES) + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_STORIES)));

        verify(storyService).updateStory(eq(2L), any(StoryUpdateDto.class));
    }

    @Test
    void shouldUpdateReviewAndRedirect() throws Exception {
        var dto = new ReviewUpdateDto();
        dto.setContent(JsonNullable.of("updated review"));

        mockMvc.perform(put(sectionPath(SECTION_REVIEWS) + "/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_REVIEWS)));

        verify(reviewService).updateReview(eq(3L), any(ReviewUpdateDto.class));
    }

    @Test
    void shouldUpdateTeamMemberAndRedirect() throws Exception {
        var dto = new TeamUpdateDto();
        dto.setFirstName(JsonNullable.of("Jane"));

        mockMvc.perform(put(sectionPath(SECTION_TEAM) + "/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_TEAM)));

        verify(teamService).updateTeamMember(eq(4L), any(TeamUpdateDto.class));
    }

    @Test
    void shouldUpdatePricingAndRedirect() throws Exception {
        var dto = new PricingUpdateDto();
        dto.setName(JsonNullable.of("Pro"));
        dto.setOriginalPrice(JsonNullable.of(199.0));

        mockMvc.perform(put(sectionPath(SECTION_PRICING) + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_PRICING)));

        verify(pricingPlanService).updatePricing(eq(5L), any(PricingUpdateDto.class));
    }

    @Test
    void shouldRejectPricingUpdateWithInvalidDiscount() throws Exception {
        var dto = new PricingUpdateDto();
        dto.setDiscountPercent(JsonNullable.of(150.0));

        mockMvc.perform(put(sectionPath(SECTION_PRICING) + "/5")
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

        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUpdatingAsNonAdmin() throws Exception {
        var dto = new ArticleUpdateDto();
        dto.setTitle(JsonNullable.of("title"));

        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // --- delete ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + "," + MARKETING_BASE_PATH + "/" + SECTION_ARTICLES,
            SECTION_STORIES + "," + MARKETING_BASE_PATH + "/" + SECTION_STORIES,
            SECTION_REVIEWS + "," + MARKETING_BASE_PATH + "/" + SECTION_REVIEWS,
            SECTION_TEAM + "," + MARKETING_BASE_PATH + "/" + SECTION_TEAM,
            SECTION_PRICING + "," + MARKETING_BASE_PATH + "/" + SECTION_PRICING
    })
    void shouldDeleteAndRedirect(String section, String redirectUrl) throws Exception {
        mockMvc.perform(delete(sectionPath(section) + "/1")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, redirectUrl));

        verifyDeleteCalledForSection(section, 1L);
    }

    @Test
    void shouldReturn401WhenDeletingWithoutAuth() throws Exception {
        mockMvc.perform(delete(sectionPath(SECTION_ARTICLES) + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenDeletingAsNonAdmin() throws Exception {
        mockMvc.perform(delete(sectionPath(SECTION_ARTICLES) + "/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // --- toggle publish / homepage ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + "," + MARKETING_BASE_PATH + "/" + SECTION_ARTICLES,
            SECTION_STORIES + "," + MARKETING_BASE_PATH + "/" + SECTION_STORIES,
            SECTION_REVIEWS + "," + MARKETING_BASE_PATH + "/" + SECTION_REVIEWS,
            SECTION_TEAM + "," + MARKETING_BASE_PATH + "/" + SECTION_TEAM
    })
    void shouldTogglePublishAndRedirect(String section, String redirectUrl) throws Exception {
        mockMvc.perform(post(sectionPath(section) + "/1/toggle-publish")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, redirectUrl));

        verifyTogglePublishCalledForSection(section, 1L);
    }

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES,
            SECTION_STORIES,
            SECTION_REVIEWS,
            SECTION_TEAM
    })
    void shouldToggleHomepageAndRedirectToHomeComponents(String section) throws Exception {
        mockMvc.perform(post(sectionPath(section) + "/1/toggle-homepage")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_HOME_COMPONENTS)));

        verifyToggleHomepageCalledForSection(section, 1L);
    }

    @Test
    void shouldReturn401WhenTogglingWithoutAuth() throws Exception {
        mockMvc.perform(post(sectionPath(SECTION_ARTICLES) + "/1/toggle-publish"))
                .andExpect(status().isUnauthorized());
    }

    // --- display order ---

    @ParameterizedTest
    @CsvSource({
            SECTION_ARTICLES + ",3",
            SECTION_STORIES + ",3",
            SECTION_REVIEWS + ",3",
            SECTION_TEAM + ",3",
            SECTION_ARTICLES + ",0"
    })
    void shouldUpdateDisplayOrderAndReturn200(String section, int order) throws Exception {
        var body = Map.of("display_order", order);

        mockMvc.perform(put(sectionPath(section) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verifyDisplayOrderCalledForSection(section, 1L, order);
    }

    @Test
    void shouldRejectNegativeDisplayOrder() throws Exception {
        var body = Map.of("display_order", -1);

        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.displayOrder").exists());
    }

    @Test
    void shouldRejectNullDisplayOrder() throws Exception {
        var body = new HashMap<String, Object>();
        body.put("display_order", null);

        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn401WhenUpdatingDisplayOrderWithoutAuth() throws Exception {
        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("display_order", 1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUpdatingDisplayOrderAsNonAdmin() throws Exception {
        mockMvc.perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("display_order", 1)))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // --- unknown section ---

    @Test
    void shouldReturn404ForUnknownIndexSection() throws Exception {
        mockMvc.perform(get(MARKETING_BASE_PATH + "/unknown")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownCreateFormSection() throws Exception {
        mockMvc.perform(get(MARKETING_BASE_PATH + "/unknown/create")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownEditFormSection() throws Exception {
        mockMvc.perform(get(MARKETING_BASE_PATH + "/unknown/1/edit")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownDeleteSection() throws Exception {
        mockMvc.perform(delete(MARKETING_BASE_PATH + "/unknown/1")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownTogglePublishSection() throws Exception {
        mockMvc.perform(post(MARKETING_BASE_PATH + "/unknown/1/toggle-publish")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownDisplayOrderSection() throws Exception {
        var body = Map.of("display_order", 1);

        mockMvc.perform(put(MARKETING_BASE_PATH + "/unknown/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    // --- helpers ---

    private void mockPageForSection(String section) {
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
        }
    }

    private void verifyServiceCalledForSection(String section) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).getAllArticles(any(Pageable.class));
            case SECTION_STORIES -> verify(storyService).getAllStories(any(Pageable.class));
            case SECTION_REVIEWS -> verify(reviewService).getAllReviews(any(Pageable.class));
            case SECTION_TEAM -> verify(teamService).getAllTeamMembers(any(Pageable.class));
            case SECTION_PRICING -> verify(pricingPlanService).getAllPricing(any(Pageable.class));
        }
    }

    private void mockEntityForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> when(articleService.getArticleById(id)).thenReturn(null);
            case SECTION_STORIES -> when(storyService.getStoryById(id)).thenReturn(null);
            case SECTION_REVIEWS -> when(reviewService.getReviewById(id)).thenReturn(null);
            case SECTION_TEAM -> when(teamService.getTeamMemberById(id)).thenReturn(null);
            case SECTION_PRICING -> when(pricingPlanService.getPricingById(id)).thenReturn(null);
        }
    }

    private void verifyDeleteCalledForSection(String section, Long id) {
        switch (section.strip()) {
            case SECTION_ARTICLES -> verify(articleService).deleteArticle(id);
            case SECTION_STORIES -> verify(storyService).deleteStory(id);
            case SECTION_REVIEWS -> verify(reviewService).deleteReview(id);
            case SECTION_TEAM -> verify(teamService).deleteTeamMember(id);
            case SECTION_PRICING -> verify(pricingPlanService).deletePricing(id);
        }
    }

    private void verifyTogglePublishCalledForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).togglePublish(id);
            case SECTION_STORIES -> verify(storyService).togglePublish(id);
            case SECTION_REVIEWS -> verify(reviewService).togglePublish(id);
            case SECTION_TEAM -> verify(teamService).togglePublish(id);
        }
    }

    private void verifyToggleHomepageCalledForSection(String section, Long id) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).toggleArticleHomepageVisibility(id);
            case SECTION_STORIES -> verify(storyService).toggleStoryHomepageVisibility(id);
            case SECTION_REVIEWS -> verify(reviewService).toggleReviewHomepageVisibility(id);
            case SECTION_TEAM -> verify(teamService).toggleTeamMemberHomepageVisibility(id);
        }
    }

    private void verifyDisplayOrderCalledForSection(String section, Long id, int order) {
        switch (section) {
            case SECTION_ARTICLES -> verify(articleService).updateArticleDisplayOrder(id, order);
            case SECTION_STORIES -> verify(storyService).updateStoryDisplayOrder(id, order);
            case SECTION_REVIEWS -> verify(reviewService).updateReviewDisplayOrder(id, order);
            case SECTION_TEAM -> verify(teamService).updateTeamMemberDisplayOrder(id, order);
        }
    }
}
