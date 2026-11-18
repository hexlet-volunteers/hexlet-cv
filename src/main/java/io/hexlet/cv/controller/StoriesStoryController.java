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
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoriesStoryController implements StoriesApi {

    private static final Set<String> ALLOWED_SORT = Set.of("displayOrder", "id", "authorName");
    private static final String DEFAULT_SORT = "displayOrder";

    private static final String DIRECTION_ASC = "asc";
    private static final String DIRECTION_DESC = "desc";

    private final StoriesStoryService storiesStoryService;

    @Override
    public ResponseEntity<StoriesStoryPageResponse> getStories(Integer page, Integer size, List<String> sort) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;

        Sort sortOrder = parseSort(sort);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortOrder);

        return ResponseEntity.ok(storiesStoryService.getPublicStories(pageable));
    }

    private Sort parseSort(List<String> sortParams) {
        if (CollectionUtils.isEmpty(sortParams)) {
            return Sort.by(Sort.Direction.ASC, DEFAULT_SORT);
        }

        Sort result = Sort.unsorted();

        for (int i = 0; i < sortParams.size(); i++) {
            String param = sortParams.get(i);
            if (!StringUtils.hasText(param) || isDirection(param)) {
                continue;
            }

            String[] parts = param.split(",");
            String property = parts[0].trim();
            validateProperty(property);

            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1) {
                direction = DIRECTION_DESC.equalsIgnoreCase(parts[1].trim())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
            } else if (i + 1 < sortParams.size() && isDirection(sortParams.get(i + 1))) {
                direction = DIRECTION_DESC.equalsIgnoreCase(sortParams.get(i + 1).trim())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
            }

            result = result.and(Sort.by(direction, property));
        }

        return result.isSorted() ? result : Sort.by(Sort.Direction.ASC, DEFAULT_SORT);
    }

    private boolean isDirection(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String trimmed = value.trim();
        return DIRECTION_ASC.equalsIgnoreCase(trimmed) || DIRECTION_DESC.equalsIgnoreCase(trimmed);
    }

    private void validateProperty(String property) {
        if (!ALLOWED_SORT.contains(property)) {
            throw new PropertyReferenceException(
                    property,
                    TypeInformation.of(StoriesStory.class),
                    List.of()
            );
        }
    }
}
