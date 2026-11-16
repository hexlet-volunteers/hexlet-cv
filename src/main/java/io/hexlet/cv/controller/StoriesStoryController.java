package io.hexlet.cv.controller;

import io.hexlet.cv.dto.StoriesStoryPageResponse;
import io.hexlet.cv.model.StoriesStory;
import io.hexlet.cv.service.StoriesStoryService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class StoriesStoryController implements StoriesApi {

    private final StoriesStoryService storiesStoryService;
    private final Set<String> allowedSort = Set.of("displayOrder", "id", "authorName");
    private final String defaultSort = "displayOrder";

    @Override
    public ResponseEntity<StoriesStoryPageResponse> getStories(Integer page, Integer size, List<String> sort) {
        if(page == null || page < 0) {
            page = 0;
        }

        if (size == null || size <= 0) {
            size = 10;
        }

        Sort sotOrder = sort(sort);
        Pageable pageable = PageRequest.of(page, size, sotOrder);
        StoriesStoryPageResponse response = storiesStoryService.getPublicStories(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Sort sort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, defaultSort);
        }

        // Случай А: Если разбили ?sort=displayOrder,desc на два элемента списка: ["displayOrder", "desc"]
        if (sortParams.size() == 2
                && (sortParams.get(1).equalsIgnoreCase("asc") || sortParams.get(1).equalsIgnoreCase("desc"))) {
            String property = sortParams.get(0).trim();
            validateProperty(property);
            Sort.Direction direction = sortParams.get(1).equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return Sort.by(direction, property);
        }

        // Случай Б: элементы вида ["displayOrder,desc"] или обычные строки
        Sort result = Sort.unsorted();
        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                continue;
            }

            String[] parts = param.split(",");
            String property = parts[0].trim();
            validateProperty(property);

            Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            result = result.and(Sort.by(direction, property));
        }

        return result.isSorted() ? result : Sort.by(Sort.Direction.ASC, defaultSort);
    }

    private void validateProperty(String property) {
        if (!allowedSort.contains(property)) {
            throw new PropertyReferenceException(
                    property,
                    TypeInformation.of(StoriesStory.class),
                    List.of()
            );
        }
    }

}
