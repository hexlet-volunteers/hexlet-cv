package io.hexlet.cv.mapper;

import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.dto.reset.TokenValidationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PasswordResetMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "message", constant = "Password reset successful")
    @Mapping(target = "resetAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract PasswordResetResponseDTO toResponseDTO(String email);

    @Mapping(target = "isValid", source = "isValid")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "remainingMinutes", source = "remainingMinutes")
    public abstract TokenValidationDTO toTokenValidationDTO(boolean isValid, String email, Long remainingMinutes);

//    public PasswordResetResponseDTO toResponseDTO(String email) {
//        return PasswordResetResponseDTO.builder()
//                .email(email)
//                .message("Password reset successful")
//                .resetAt(LocalDateTime.now())
//                .build();
//    }
//
//    public TokenValidationDTO toTokenValidationDTO(boolean isValid, String email, Long remainingMinutes) {
//        return TokenValidationDTO.builder()
//                .isValid(isValid)
//                .email(email)
//                .remainingMinutes(remainingMinutes)
//                .build();
//    }
}
