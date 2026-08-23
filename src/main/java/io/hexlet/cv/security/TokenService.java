package io.hexlet.cv.security;

import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;

    public Tokens authenticateAndGenerate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return new Tokens(
                jwtUtils.generateAccessToken(email),
                jwtUtils.generateRefreshToken(email)
        );
    }

    public Tokens refresh(String refreshToken) {
        Jwt jwt;
        try {
            jwt = jwtUtils.decodeRefresh(refreshToken);
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid refresh token", e);
        }
        String email = jwt.getSubject();
        return new Tokens(
                jwtUtils.generateAccessToken(email),
                jwtUtils.generateRefreshToken(email)
        );
    }

    @Transactional
    public void revokeByRefreshToken(String refreshToken) {
        try {
            Jwt jwt = jwtUtils.decodeRefresh(refreshToken);
            userRepository.incrementTokenVersion(jwt.getSubject());
        } catch (JwtException ignored) {
        }
    }

    public record Tokens(String access, String refresh) {
    }
}
