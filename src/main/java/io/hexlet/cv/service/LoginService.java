package io.hexlet.cv.service;

import io.hexlet.cv.dto.user.auth.LoginRequestDTO;
import io.hexlet.cv.dto.user.auth.LoginResponseDTO;
import io.hexlet.cv.handler.exception.InvalidPasswordException;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.mapper.LoginMapper;
import io.hexlet.cv.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private UserRepository userRepository;

    private LoginMapper loginMapper;

    private BCryptPasswordEncoder encoder;

    public LoginResponseDTO login(LoginRequestDTO inputDTO) {

        var foundUser = userRepository.findByEmail(inputDTO.getEmail()).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден")
        );

        if (!encoder.matches(inputDTO.getPassword(), foundUser.getPassword())) {
            throw new InvalidPasswordException("Неверный пароль");
        }

        return loginMapper.map(foundUser);
    }
}
