package io.hexlet.cv.util;

import io.hexlet.cv.config.JwtProperties;
import io.hexlet.cv.model.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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

    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_TOKEN_VERSION = "tokenVersion";
    public static final String CLAIM_FAMILY_ID = "familyId";
    public static final String TYPE_REFRESH = "refresh";

    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;
    private final JwtDecoder jwtDecoder;
    @Qualifier("refreshTokenDecoder")
    private final JwtDecoder refreshTokenDecoder;

    public String generateAccessToken(User user, UUID familyId) {
        JwtClaimsSet claims = baseClaims(user, jwtProperties.getAccessTokenValiditySeconds())
                .claim("roles", List.of(user.getRole().name()))
                .claim(CLAIM_FAMILY_ID, familyId.toString())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(User user, UUID jti, UUID familyId) {
        JwtClaimsSet claims = baseClaims(user, jwtProperties.getRefreshTokenValiditySeconds())
                .id(jti.toString())
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .claim(CLAIM_FAMILY_ID, familyId.toString())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private JwtClaimsSet.Builder baseClaims(User user, long validitySeconds) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .audience(List.of(jwtProperties.getAudience()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(validitySeconds))
                .subject(user.getEmail())
                .claim(CLAIM_TOKEN_VERSION, user.getTokenVersion());
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public Jwt decodeRefresh(String token) {
        return refreshTokenDecoder.decode(token);
    }
}
