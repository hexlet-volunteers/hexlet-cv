package io.hexlet.cv.converter;

import io.hexlet.cv.dto.UserResponseDTO;
import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.dto.reset.TokenValidationDTO;
import io.hexlet.cv.model.User;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public PasswordResetResponseDTO toResponseDTO(String email) {
        if (email == null) {
            return null;
        }

        PasswordResetResponseDTO dto = new PasswordResetResponseDTO();
        dto.setEmail(email);
        dto.setMessage("Password reset successful");
        dto.setResetAt(LocalDateTime.now());

        return dto;
    }

    public TokenValidationDTO toTokenValidationDTO(boolean isValid, String email, Long remainingMinutes) {
        TokenValidationDTO dto = new TokenValidationDTO();
        dto.setValid(isValid);
        dto.setEmail(email);
        dto.setRemainingMinutes(remainingMinutes);

        return dto;
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastSignInAt())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .state(user.getState())
                .build();
    }

}
