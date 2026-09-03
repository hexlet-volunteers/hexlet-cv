package io.hexlet.cv.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.dto.StoriesStoryDto;
import io.hexlet.cv.dto.StoriesStoryPageResponse;
import io.hexlet.cv.model.StoriesStory;
import io.hexlet.cv.repository.StoriesStoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class StoriesStoryControllerTest {

    private static final String BASE_URL = "/api/v1/stories";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoriesStoryRepository storiesStoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        storiesStoryRepository.deleteAll();
    }

    private StoriesStory createRandomStory(boolean isPublished, int displayOrder) {
        String uniqueId = UUID.randomUUID().toString();

        return storiesStoryRepository.save(
                StoriesStory.builder()
                        .authorName("Студент_" + uniqueId)
                        .avatarUrl("https://example.com_" + uniqueId + ".jpg")
                        .companyName("Компания_" + uniqueId)
                        .offerPosition("Разработчик_" + uniqueId)
                        .text("Тестовый текст отзыва номер " + uniqueId)
                        .displayOrder(displayOrder)
                        .isPublished(isPublished)
                        .build()
        );
    }

    private void createStories() {
        createRandomStory(true, 1);
        createRandomStory(true, 2);
        createRandomStory(false, 3);
    }

    @Test
    @DisplayName("GET /api/v1/stories — пустой список опубликованных историй")
    void testGetStoriesEmpty() throws Exception {
        var response = mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        StoriesStoryPageResponse actualPage =
                objectMapper.readValue(
                        response.getContentAsString(),
                        StoriesStoryPageResponse.class
                );

        assertThat(actualPage.getContent()).isEmpty();
        assertThat(actualPage.getTotalElements()).isZero();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("storiesArguments")
    @DisplayName("GET /api/v1/stories — получение историй")
    void testGetStories(
            String testCase,
            Map<String, String> params,
            HttpStatus expectedStatus,
            List<Integer> expectedDisplayOrders
    ) throws Exception {

        createStories();

        MockHttpServletRequestBuilder request = get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON);

        params.forEach(request::param);

        var response = mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()))
                .andReturn()
                .getResponse();

        if (expectedDisplayOrders != null) {
            StoriesStoryPageResponse actualPage =
                    objectMapper.readValue(
                            response.getContentAsString(),
                            StoriesStoryPageResponse.class
                    );

            assertThat(actualPage.getContent())
                    .extracting(StoriesStoryDto::getDisplayOrder)
                    .containsExactlyElementsOf(expectedDisplayOrders);

            assertThat(actualPage.getTotalElements())
                    .isEqualTo(2);

            assertThat(actualPage.getNumber())
                    .isZero();
        }
    }

    private static Stream<Arguments> storiesArguments() {
        return Stream.of(
                Arguments.of(
                        "default sorting",
                        Map.of(),
                        HttpStatus.OK,
                        List.of(1, 2)
                ),
                Arguments.of(
                        "custom sorting desc with pagination",
                        Map.of(
                                "page", "0",
                                "size", "1",
                                "sort", "displayOrder,desc"
                        ),
                        HttpStatus.OK,
                        List.of(2)
                ),
                Arguments.of(
                        "invalid sort field",
                        Map.of(
                                "sort", "unknownField,asc"
                        ),
                        HttpStatus.BAD_REQUEST,
                        null
                )
        );
    }
}