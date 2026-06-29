package io.hexlet.cv.controller.admin.marketing;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.marketing.DisplayOrderRequest;
import io.hexlet.cv.dto.marketing.ArticleCreateDto;
import io.hexlet.cv.dto.marketing.PricingCreateDto;
import io.hexlet.cv.dto.marketing.ReviewCreateDto;
import io.hexlet.cv.dto.marketing.StoryCreateDto;
import io.hexlet.cv.dto.marketing.TeamCreateDto;
import io.hexlet.cv.dto.marketing.ArticleUpdateDto;
import io.hexlet.cv.dto.marketing.PricingUpdateDto;
import io.hexlet.cv.dto.marketing.ReviewUpdateDto;
import io.hexlet.cv.dto.marketing.StoryUpdateDto;
import io.hexlet.cv.dto.marketing.TeamUpdateDto;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.service.ArticleService;
import io.hexlet.cv.service.EnumService;
import io.hexlet.cv.service.PricingPlanService;
import io.hexlet.cv.service.ReviewService;
import io.hexlet.cv.service.StoryService;
import io.hexlet.cv.service.TeamService;
import io.hexlet.cv.util.ControllerUtils;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/marketing")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMarketingController {

    private static final String SECTION_ARTICLES = "articles";
    private static final String SECTION_STORIES = "stories";
    private static final String SECTION_REVIEWS = "reviews";
    private static final String SECTION_TEAM = "team";
    private static final String SECTION_PRICING = "pricing";
    private static final String SECTION_HOME_COMPONENTS = "home-components";
    private static final String MARKETING_BASE_PATH = "/admin/marketing";

    private ControllerUtils controllerUtils;
    private Inertia inertia;
    private EnumService enumService;
    private ArticleService articleService;
    private StoryService storyService;
    private ReviewService reviewService;
    private TeamService teamService;
    private PricingPlanService pricingPlanService;

    private Map<String, Object> createTeamFormProps(String subSection) {
        Map<String, Object> props = new HashMap<>(controllerUtils.createMarketingProps(subSection));
        props.putAll(enumService.getTeamEnums());
        return props;
    }

    @GetMapping("/{section}")
    public ResponseEntity<String> index(@PathVariable String section,
                                        @PageableDefault(size = 20) Pageable pageable) {
        log.debug("[MARKETING] Getting section: {} for locale: {}", section, pageable);

        return switch (section) {
            case SECTION_ARTICLES -> {
                var articlesPage = articleService.getAllArticles(pageable);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_ARTICLES));
                props.put(SECTION_ARTICLES, articlesPage.getContent());
                props.put("pagination", controllerUtils.createPaginationMap(articlesPage, pageable));
                log.debug("[MARKETING] Rendering articles page with {} items",
                        articlesPage.getContent().size());
                yield inertia.render("Admin/Marketing/Articles/Index", props);
            }
            case SECTION_STORIES -> {
                var storiesPage = storyService.getAllStories(pageable);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_STORIES));
                props.put(SECTION_STORIES, storiesPage.getContent());
                props.put("pagination", controllerUtils.createPaginationMap(storiesPage, pageable));
                log.debug("[MARKETING] Rendering stories page with {} items",
                        storiesPage.getContent().size());
                yield inertia.render("Admin/Marketing/Stories/Index", props);
            }
            case SECTION_REVIEWS -> {
                var reviewsPage = reviewService.getAllReviews(pageable);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_REVIEWS));
                props.put(SECTION_REVIEWS, reviewsPage.getContent());
                props.put("pagination", controllerUtils.createPaginationMap(reviewsPage, pageable));
                log.debug("[MARKETING] Rendering reviews page with {} items",
                        reviewsPage.getContent().size());
                yield inertia.render("Admin/Marketing/Reviews/Index", props);
            }
            case SECTION_TEAM -> {
                var teamsPage = teamService.getAllTeamMembers(pageable);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_TEAM));
                props.put(SECTION_TEAM, teamsPage.getContent());
                props.put("pagination", controllerUtils.createPaginationMap(teamsPage, pageable));
                log.debug("[MARKETING] Rendering teams page with {} items",
                        teamsPage.getContent().size());
                yield inertia.render("Admin/Marketing/Team/Index", props);
            }
            case SECTION_PRICING -> {
                var pricingPage = pricingPlanService.getAllPricing(pageable);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_PRICING));
                props.put(SECTION_PRICING, pricingPage.getContent());
                props.put("pagination", controllerUtils.createPaginationMap(pricingPage, pageable));
                log.debug("[MARKETING] Rendering pricing page with {} items",
                        pricingPage.getContent().size());
                yield inertia.render("Admin/Marketing/Pricing/Index", props);
            }
            case SECTION_HOME_COMPONENTS -> {
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_HOME_COMPONENTS));
                props.put(SECTION_ARTICLES, articleService.getHomepageArticles());
                props.put(SECTION_STORIES, storyService.getHomepageStories());
                props.put(SECTION_REVIEWS, reviewService.getHomepageReviews());
                props.put(SECTION_TEAM, teamService.getHomepageTeamMembers());
                log.debug("[MARKETING] Rendering home components page");
                yield inertia.render("Admin/Marketing/HomeComponents/Index", props);
            }
            default -> throw new ResourceNotFoundException("section.not.found");
        };
    }

    @GetMapping("/")
    public ResponseEntity<String> defaultSection() {
        log.debug("[MARKETING] Redirect to default section");
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_ARTICLES);
    }

    @GetMapping("/{section}/create")
    public ResponseEntity<String> createForm(@PathVariable String section) {
        log.debug("[MARKETING] Create form for section: {}", section);

        return switch (section) {
            case SECTION_ARTICLES -> inertia.render("Admin/Marketing/Articles/Create",
                    controllerUtils.createMarketingProps(SECTION_ARTICLES));
            case SECTION_STORIES -> inertia.render("Admin/Marketing/Stories/Create",
                    controllerUtils.createMarketingProps(SECTION_STORIES));
            case SECTION_REVIEWS -> inertia.render("Admin/Marketing/Reviews/Create",
                    controllerUtils.createMarketingProps(SECTION_REVIEWS));
            case SECTION_TEAM -> inertia.render("Admin/Marketing/Team/Create",
                    createTeamFormProps(SECTION_TEAM));
            case SECTION_PRICING -> inertia.render("Admin/Marketing/Pricing/Create",
                    controllerUtils.createMarketingProps(SECTION_PRICING));
            default -> throw new ResourceNotFoundException("Create form not found for section: " + section);
        };
    }

    @GetMapping("/{section}/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> editForm(@PathVariable String section, @PathVariable Long id) {
        log.debug("[MARKETING] Edit form for {} with id: {}", section, id);

        return switch (section) {
            case SECTION_ARTICLES -> {
                var article = articleService.getArticleById(id);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_ARTICLES));
                props.put("article", article);
                yield inertia.render("Admin/Marketing/Articles/Edit", props);
            }
            case SECTION_STORIES -> {
                var story = storyService.getStoryById(id);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_STORIES));
                props.put("story", story);
                yield inertia.render("Admin/Marketing/Stories/Edit", props);
            }
            case SECTION_REVIEWS -> {
                var review = reviewService.getReviewById(id);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_REVIEWS));
                props.put("review", review);
                yield inertia.render("Admin/Marketing/Reviews/Edit", props);
            }
            case SECTION_TEAM -> {
                var teamMember = teamService.getTeamMemberById(id);
                var props = new HashMap<>(createTeamFormProps(SECTION_TEAM));
                props.put("teamMember", teamMember);
                yield inertia.render("Admin/Marketing/Team/Edit", props);
            }
            case SECTION_PRICING -> {
                var pricing = pricingPlanService.getPricingById(id);
                var props = new HashMap<>(controllerUtils.createMarketingProps(SECTION_PRICING));
                props.put(SECTION_PRICING, pricing);
                yield inertia.render("Admin/Marketing/Pricing/Edit", props);
            }
            default -> throw new ResourceNotFoundException("Edit form not found for section: " + section);
        };
    }

    @PostMapping("/articles")
    public ResponseEntity<String> createArticle(@Valid @RequestBody ArticleCreateDto createDTO) {
        log.debug("[MARKETING] Creating article");

        articleService.createArticle(createDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_ARTICLES);
    }

    @PostMapping("/stories")
    public ResponseEntity<String> createStory(@Valid @RequestBody StoryCreateDto createDTO) {
        log.debug("[MARKETING] Creating story");

        storyService.createStory(createDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_STORIES);
    }

    @PostMapping("/reviews")
    public ResponseEntity<String> createReview(@Valid @RequestBody ReviewCreateDto createDTO) {
        log.debug("[MARKETING] Creating review");

        reviewService.createReview(createDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_REVIEWS);
    }

    @PostMapping("/team")
    public ResponseEntity<String> createTeamMember(@Valid @RequestBody TeamCreateDto createDTO) {
        log.debug("[MARKETING] Creating team member");

        teamService.createTeamMember(createDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_TEAM);
    }

    @PostMapping("/pricing")
    public ResponseEntity<String> createPricing(@Valid @RequestBody PricingCreateDto createDTO) {
        log.debug("[MARKETING] Creating pricing plan");

        pricingPlanService.createPricing(createDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_PRICING);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<String> updateArticle(@PathVariable Long id,
                                                @Valid @RequestBody ArticleUpdateDto updateDTO) {
        log.debug("[MARKETING] Updating article id: {}", id);

        articleService.updateArticle(id, updateDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_ARTICLES);
    }

    @PutMapping("/stories/{id}")
    public ResponseEntity<String> updateStory(@PathVariable Long id,
                                              @Valid @RequestBody StoryUpdateDto updateDTO) {
        log.debug("[MARKETING] Updating story id: {}", id);

        storyService.updateStory(id, updateDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_STORIES);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<String> updateReview(@PathVariable Long id,
                                               @Valid @RequestBody ReviewUpdateDto updateDTO) {
        log.debug("[MARKETING] Updating review id: {}", id);

        reviewService.updateReview(id, updateDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_REVIEWS);
    }

    @PutMapping("/team/{id}")
    public ResponseEntity<String> updateTeamMember(@PathVariable Long id,
                                                   @Valid @RequestBody TeamUpdateDto updateDTO) {
        log.debug("[MARKETING] Updating team member id: {}", id);

        teamService.updateTeamMember(id, updateDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_TEAM);
    }

    @PutMapping("/pricing/{id}")
    public ResponseEntity<String> updatePricing(@PathVariable Long id,
                                                @Valid @RequestBody PricingUpdateDto updateDTO) {
        log.debug("[MARKETING] Updating pricing plan id: {}", id);

        pricingPlanService.updatePricing(id, updateDTO);
        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_PRICING);
    }

    @DeleteMapping("/{section}/{id}")
    public ResponseEntity<String> delete(@PathVariable String section,
                                         @PathVariable Long id) {
        log.debug("[MARKETING] Deleting {} with id: {}", section, id);

        switch (section) {
            case SECTION_ARTICLES -> articleService.deleteArticle(id);
            case SECTION_STORIES -> storyService.deleteStory(id);
            case SECTION_REVIEWS -> reviewService.deleteReview(id);
            case SECTION_TEAM -> teamService.deleteTeamMember(id);
            case SECTION_PRICING -> pricingPlanService.deletePricing(id);
            default -> throw new ResourceNotFoundException("Section not found: " + section);
        }

        return inertia.redirect(MARKETING_BASE_PATH + "/" + section);
    }

    @PostMapping("/{section}/{id}/toggle-publish")
    public ResponseEntity<String> togglePublish(@PathVariable String section,
                                                @PathVariable Long id) {
        log.debug("[MARKETING] Toggling publish for {} id: {}", section, id);

        switch (section) {
            case SECTION_ARTICLES -> articleService.togglePublish(id);
            case SECTION_STORIES -> storyService.togglePublish(id);
            case SECTION_REVIEWS -> reviewService.togglePublish(id);
            case SECTION_TEAM -> teamService.togglePublish(id);
            default -> throw new ResourceNotFoundException("Section not found: " + section);
        }

        return inertia.redirect(MARKETING_BASE_PATH + "/" + section);
    }

    @PostMapping("/{section}/{id}/toggle-homepage")
    public ResponseEntity<String> toggleHomepage(@PathVariable String section,
                                                 @PathVariable Long id) {
        log.debug("[MARKETING] Toggling homepage for {} id: {}", section, id);

        switch (section) {
            case SECTION_ARTICLES -> articleService.toggleArticleHomepageVisibility(id);
            case SECTION_STORIES -> storyService.toggleStoryHomepageVisibility(id);
            case SECTION_REVIEWS -> reviewService.toggleReviewHomepageVisibility(id);
            case SECTION_TEAM -> teamService.toggleTeamMemberHomepageVisibility(id);
            default -> throw new ResourceNotFoundException("Section not found: " + section);
        }

        return inertia.redirect(MARKETING_BASE_PATH + "/" + SECTION_HOME_COMPONENTS);
    }

    @PutMapping("/{section}/{id}/display-order")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updateDisplayOrder(@PathVariable String section,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody DisplayOrderRequest request) {
        log.debug("[MARKETING] Updating display order for {} id: {}", section, id);

        Integer displayOrder = request.getDisplayOrder();

        switch (section) {
            case SECTION_ARTICLES -> articleService.updateArticleDisplayOrder(id, displayOrder);
            case SECTION_STORIES -> storyService.updateStoryDisplayOrder(id, displayOrder);
            case SECTION_REVIEWS -> reviewService.updateReviewDisplayOrder(id, displayOrder);
            case SECTION_TEAM -> teamService.updateTeamMemberDisplayOrder(id, displayOrder);
            default -> throw new ResourceNotFoundException("Section not found: " + section);
        }

        return ResponseEntity.ok().build();
    }
}
