package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import io.hexlet.cv.dto.marketing.ArticleCreateDto;
import io.hexlet.cv.dto.marketing.ArticleUpdateDto;
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
 * Создание и обновление статей через {@code POST/PUT /admin/marketing/articles}:
 * успешные запросы, валидация DTO и проверки авторизации.
 */
public class AdminMarketingArticlesControllerTest extends AdminMarketingControllerTestSupport {

    @Test
    void shouldCreateArticleAndRedirect() throws Exception {
        var dto = new ArticleCreateDto();
        dto.setTitle("test title");
        dto.setAuthor("test author");
        dto.setContent("test content");

        mockMvc().perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_ARTICLES)));

        ArgumentCaptor<ArticleCreateDto> captor = ArgumentCaptor.forClass(ArticleCreateDto.class);
        verify(articleService()).createArticle(captor.capture());
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

        mockMvc().perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
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

        mockMvc().perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
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

        mockMvc().perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenCreatingArticleAsNonAdmin() throws Exception {
        var dto = new ArticleCreateDto();
        dto.setTitle("title");
        dto.setAuthor("author");
        dto.setContent("content");

        mockMvc().perform(post(sectionPath(SECTION_ARTICLES))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateArticleAndRedirect() throws Exception {
        var dto = new ArticleUpdateDto();
        dto.setTitle(JsonNullable.of("updated title"));
        dto.setContent(JsonNullable.of("updated content"));

        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_ARTICLES)));

        verify(articleService()).updateArticle(eq(1L), any(ArticleUpdateDto.class));
    }

    @Test
    void shouldReturn401WhenUpdatingWithoutAuth() throws Exception {
        var dto = new ArticleUpdateDto();
        dto.setTitle(JsonNullable.of("title"));

        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUpdatingAsNonAdmin() throws Exception {
        var dto = new ArticleUpdateDto();
        dto.setTitle(JsonNullable.of("title"));

        mockMvc().perform(put(sectionPath(SECTION_ARTICLES) + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
