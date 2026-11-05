package io.hexlet.cv.dto.resume;

import io.hexlet.cv.model.User;

// Автор резюме в публичной выдаче: только имя, без контактов и email.
public record AuthorDTO(Long id, String firstName, String lastName) {

    public static AuthorDTO from(User user) {
        return new AuthorDTO(user.getId(), user.getFirstName(), user.getLastName());
    }
}
