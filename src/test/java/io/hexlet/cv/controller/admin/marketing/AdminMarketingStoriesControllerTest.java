package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import io.hexlet.cv.dto.marketing.StoryCreateDto;
import io.hexlet.cv.dto.marketing.StoryUpdateDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Создание и обновление историй через {@code POST/PUT /admin/marketing/stories}:
 * успешные запросы и валидация DTO (title, showOnHomepage, imageUrl).
 */
public class AdminMarketingStoriesControllerTest extends AdminMarketingControllerTestSupport {

    @Test
    void shouldCreateStoryAndRedirect() throws Exception {
        var dto = new StoryCreateDto();
        dto.setTitle("story title");
        dto.setContent("story content");
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_STORIES)));

        ArgumentCaptor<StoryCreateDto> captor = ArgumentCaptor.forClass(StoryCreateDto.class);
        verify(storyService()).createStory(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("story title");
        assertThat(captor.getValue().getContent()).isEqualTo("story content");
    }

    @Test
    void shouldRejectStoryWithBlankTitle() throws Exception {
        var dto = new StoryCreateDto();
        dto.setTitle("");
        dto.setContent("content");
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void shouldRejectStoryWithMissingShowOnHomepage() throws Exception {
        var dto = new StoryCreateDto();
        dto.setTitle("title");
        dto.setContent("content");

        mockMvc().perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
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

        mockMvc().perform(post(sectionPath(SECTION_STORIES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.imageUrl").exists());
    }

    @Test
    void shouldUpdateStoryAndRedirect() throws Exception {
        var dto = new StoryUpdateDto();
        dto.setTitle(JsonNullable.of("updated story"));

        mockMvc().perform(put(sectionPath(SECTION_STORIES) + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_STORIES)));

        verify(storyService()).updateStory(eq(2L), any(StoryUpdateDto.class));
    }
}
