package io.hexlet.cv.mapper;

import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import io.hexlet.cv.dto.user.auth.RegistrationResponseDTO;
import io.hexlet.cv.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {
    JsonNullableMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy
        .IGNORE, componentModel = MappingConstants.ComponentModel
        .SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RegistrationMapper {
    public abstract User map(RegistrationRequestDTO dto);

    @Mapping(target = "role", expression = "java(user.getRole().name().toLowerCase())")
    public abstract RegistrationResponseDTO map(User user);
}
