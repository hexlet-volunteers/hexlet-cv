package io.hexlet.cv.dto.api;

import io.hexlet.cv.model.User;

// Текущий пользователь — ответ на вход, регистрацию и `GET /api/auth/me`.
public record CurrentUserDTO(Long id, String email, String firstName, String lastName, String role) {

    public static CurrentUserDTO from(User user) {
        return new CurrentUserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() == null ? null : user.getRole().name());
    }
}
