package io.hexlet.cv.util;

import io.hexlet.cv.config.JwtProperties;
import io.hexlet.cv.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTUtils {

    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;
    private final JwtDecoder jwtDecoder;
    @Qualifier("refreshTokenDecoder")
    private final JwtDecoder refreshTokenDecoder;
    private final UserRepository userRepository;

    public String generateAccessToken(String username) {
        var user = userRepository.findByEmail(username).orElseThrow();
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .audience(List.of(jwtProperties.getAudience()))
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.getAccessTokenValiditySeconds(), ChronoUnit.SECONDS))
                .subject(username)
                .claim("roles", List.of(user.getRole().name()))
                .claim("tokenVersion", user.getTokenVersion())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(String username) {
        var user = userRepository.findByEmail(username).orElseThrow();
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .audience(List.of(jwtProperties.getAudience()))
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.getRefreshTokenValiditySeconds(), ChronoUnit.SECONDS))
                .subject(username)
                .claim("type", "refresh")
                .claim("tokenVersion", user.getTokenVersion())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public Jwt decodeRefresh(String token) {
        return refreshTokenDecoder.decode(token);
    }
}
