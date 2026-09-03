package io.hexlet.cv.mapper;

import io.hexlet.cv.dto.StoriesStoryDto;
import io.hexlet.cv.model.StoriesStory;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class StoriesStoryMapper {

    public abstract StoriesStoryDto map(StoriesStory model);
    public abstract List<StoriesStoryDto> map(List<StoriesStory> models);
}
