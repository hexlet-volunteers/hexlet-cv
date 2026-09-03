package io.hexlet.cv.controller;

import io.hexlet.cv.dto.StoriesStoryPageResponse;
import io.hexlet.cv.service.StoriesStoryService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stories")
public class StoriesStoryController {

    private final StoriesStoryService storiesStoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<StoriesStoryPageResponse> getStories(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "displayOrder",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(storiesStoryService.getPublicStories(pageable));
    }
}