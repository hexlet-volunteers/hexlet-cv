package io.hexlet.cv.util;

import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private UserRepository userRepository;

    /**
     * Метод возвращает текущего аутентифицированного пользователя.
     * Метод извлекает email из контекста аутентификации Spring Security
     * и загружает соответствующего пользователя из базы данных.
     * @return текущий {@link User} из репозитория по email из контекста аутентификации
     * @throws InsufficientAuthenticationException если пользователь не аутентифицирован
     * @throws UserNotFoundException если пользователь не найден в базе данных
     */

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("User is not authenticated");
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isAuthor(long userId) {
        var userAuthorEmail = userRepository.findById(userId).get().getEmail();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return userAuthorEmail.equals(authentication.getName());
    }
}
