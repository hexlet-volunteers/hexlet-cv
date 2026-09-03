package io.hexlet.cv.service;

import java.util.List;

import io.hexlet.cv.dto.StoriesStoryDto;
import io.hexlet.cv.dto.StoriesStoryPageResponse;
import io.hexlet.cv.mapper.StoriesStoryMapper;
import io.hexlet.cv.model.StoriesStory;
import io.hexlet.cv.repository.StoriesStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoriesStoryService {

    private final StoriesStoryRepository storiesStoryRepository;
    private final StoriesStoryMapper storiesStoryMapper;

    @Transactional(readOnly = true)
    public StoriesStoryPageResponse getPublicStories(Pageable pageable) {
        Page<StoriesStory> storiesPage = storiesStoryRepository.findByIsPublishedTrue(pageable);

        List<StoriesStoryDto> storyDtos = storiesStoryMapper.map(storiesPage.getContent());

        return new StoriesStoryPageResponse(
                storyDtos,
                storiesPage.getTotalElements(),
                storiesPage.getTotalPages(),
                storiesPage.getNumber()
        );
    }
}