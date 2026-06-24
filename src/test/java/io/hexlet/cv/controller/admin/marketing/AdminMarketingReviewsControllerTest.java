package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import io.hexlet.cv.dto.marketing.ReviewCreateDto;
import io.hexlet.cv.dto.marketing.ReviewUpdateDto;
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
 * Создание и обновление отзывов через {@code POST/PUT /admin/marketing/reviews}:
 * успешные запросы и валидация DTO (author, avatarUrl).
 */
public class AdminMarketingReviewsControllerTest extends AdminMarketingControllerTestSupport {

    @Test
    void shouldCreateReviewAndRedirect() throws Exception {
        var dto = new ReviewCreateDto();
        dto.setAuthor("reviewer");
        dto.setContent("review content");
        dto.setShowOnHomepage(true);

        mockMvc().perform(post(sectionPath(SECTION_REVIEWS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_REVIEWS)));

        ArgumentCaptor<ReviewCreateDto> captor = ArgumentCaptor.forClass(ReviewCreateDto.class);
        verify(reviewService()).createReview(captor.capture());
        assertThat(captor.getValue().getAuthor()).isEqualTo("reviewer");
        assertThat(captor.getValue().getContent()).isEqualTo("review content");
    }

    @Test
    void shouldRejectReviewWithBlankAuthor() throws Exception {
        var dto = new ReviewCreateDto();
        dto.setAuthor("");
        dto.setContent("content");
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_REVIEWS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
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

        mockMvc().perform(post(sectionPath(SECTION_REVIEWS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.avatarUrl").exists());
    }

    @Test
    void shouldUpdateReviewAndRedirect() throws Exception {
        var dto = new ReviewUpdateDto();
        dto.setContent(JsonNullable.of("updated review"));

        mockMvc().perform(put(sectionPath(SECTION_REVIEWS) + "/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_REVIEWS)));

        verify(reviewService()).updateReview(eq(3L), any(ReviewUpdateDto.class));
    }
}
