package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Общие маршруты {@code AdminMarketingController}, не привязанные к одной сущности:
 * списки и формы (index / create / edit), delete, toggle-publish, toggle-homepage,
 * display-order, 404 для неизвестных секций и проверки авторизации.
 */
public class AdminMarketingCommonControllerTest extends AdminMarketingControllerTestSupport {

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

        mockMvc().perform(get(sectionPath(section))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verifyServiceCalledForSection(section);
        verify(controllerUtils()).createMarketingProps(section);
        verify(controllerUtils()).createPaginationMap(any(Page.class), any(Pageable.class));

        ArgumentCaptor<Map<String, Object>> propsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(inertia()).render(eq(componentName), propsCaptor.capture());
        assertThat(propsCaptor.getValue()).containsKeys(propKey, "pagination");
    }

    @Test
    void shouldReturn401ForUnauthenticatedIndexRequest() throws Exception {
        mockMvc().perform(get(sectionPath(SECTION_ARTICLES)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403ForNonAdminIndexRequest() throws Exception {
        mockPageForSection(SECTION_ARTICLES);
        mockMvc().perform(get(sectionPath(SECTION_ARTICLES))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
        SECTION_ARTICLES + ", Admin/Marketing/Articles/Create",
        SECTION_STORIES + ",  Admin/Marketing/Stories/Create",
        SECTION_REVIEWS + ",  Admin/Marketing/Reviews/Create",
        SECTION_TEAM + ",     Admin/Marketing/Team/Create",
        SECTION_PRICING + ",  Admin/Marketing/Pricing/Create"
    })
    void shouldRenderCreatePageForSection(String section, String componentName) throws Exception {
        mockMvc().perform(get(sectionPath(section) + "/create")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(controllerUtils()).createMarketingProps(section);
        verify(inertia()).render(eq(componentName), anyMap());
        if (section.equals(SECTION_TEAM)) {
            verify(enumService()).getTeamEnums();
        }
    }

    @Test
    void shouldReturn401ForUnauthenticatedCreateFormRequest() throws Exception {
        mockMvc().perform(get(sectionPath(SECTION_ARTICLES) + "/create"))
                .andExpect(status().isUnauthorized());
    }

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

        mockMvc().perform(get(sectionPath(section) + "/1/edit")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verify(inertia()).render(eq(componentName), anyMap());
    }

    @Test
    void shouldReturn401ForUnauthenticatedEditFormRequest() throws Exception {
        mockMvc().perform(get(sectionPath(SECTION_ARTICLES) + "/1/edit"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @CsvSource({
        SECTION_ARTICLES + "," + MARKETING_BASE_PATH + "/" + SECTION_ARTICLES,
        SECTION_STORIES + "," + MARKETING_BASE_PATH + "/" + SECTION_STORIES,
        SECTION_REVIEWS + "," + MARKETING_BASE_PATH + "/" + SECTION_REVIEWS,
        SECTION_TEAM + "," + MARKETING_BASE_PATH + "/" + SECTION_TEAM,
        SECTION_PRICING + "," + MARKETING_BASE_PATH + "/" + SECTION_PRICING
    })
    void shouldDeleteAndRedirect(String section, String redirectUrl) throws Exception {
        mockMvc().perform(delete(sectionPath(section) + "/1")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, redirectUrl));

        verifyDeleteCalledForSection(section, 1L);
    }

    @Test
    void shouldReturn401WhenDeletingWithoutAuth() throws Exception {
        mockMvc().perform(delete(sectionPath(SECTION_ARTICLES) + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenDeletingAsNonAdmin() throws Exception {
        mockMvc().perform(delete(sectionPath(SECTION_ARTICLES) + "/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
        SECTION_ARTICLES + "," + MARKETING_BASE_PATH + "/" + SECTION_ARTICLES,
        SECTION_STORIES + "," + MARKETING_BASE_PATH + "/" + SECTION_STORIES,
        SECTION_REVIEWS + "," + MARKETING_BASE_PATH + "/" + SECTION_REVIEWS,
        SECTION_TEAM + "," + MARKETING_BASE_PATH + "/" + SECTION_TEAM
    })
    void shouldTogglePublishAndRedirect(String section, String redirectUrl) throws Exception {
        mockMvc().perform(post(sectionPath(section) + "/1/toggle-publish")
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
        mockMvc().perform(post(sectionPath(section) + "/1/toggle-homepage")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_HOME_COMPONENTS)));

        verifyToggleHomepageCalledForSection(section, 1L);
    }

    @Test
    void shouldReturn401WhenTogglingWithoutAuth() throws Exception {
        mockMvc().perform(post(sectionPath(SECTION_ARTICLES) + "/1/toggle-publish"))
                .andExpect(status().isUnauthorized());
    }

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

        mockMvc().perform(put(sectionPath(section) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isOk());

        verifyDisplayOrderCalledForSection(section, 1L, order);
    }

    @Test
    void shouldRejectNegativeDisplayOrder() throws Exception {
        var body = Map.of("display_order", -1);

        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.displayOrder").exists());
    }

    @Test
    void shouldRejectNullDisplayOrder() throws Exception {
        var body = new HashMap<String, Object>();
        body.put("display_order", null);

        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn401WhenUpdatingDisplayOrderWithoutAuth() throws Exception {
        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(Map.of("display_order", 1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUpdatingDisplayOrderAsNonAdmin() throws Exception {
        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(Map.of("display_order", 1)))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404ForUnknownIndexSection() throws Exception {
        mockMvc().perform(get(MARKETING_BASE_PATH + "/unknown")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownCreateFormSection() throws Exception {
        mockMvc().perform(get(MARKETING_BASE_PATH + "/unknown/create")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownEditFormSection() throws Exception {
        mockMvc().perform(get(MARKETING_BASE_PATH + "/unknown/1/edit")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownDeleteSection() throws Exception {
        mockMvc().perform(delete(MARKETING_BASE_PATH + "/unknown/1")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownTogglePublishSection() throws Exception {
        mockMvc().perform(post(MARKETING_BASE_PATH + "/unknown/1/toggle-publish")
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForUnknownDisplayOrderSection() throws Exception {
        var body = Map.of("display_order", 1);

        mockMvc().perform(put(MARKETING_BASE_PATH + "/unknown/1/display-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(body))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isNotFound());
    }
}
