package io.hexlet.cv.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.hexlet.cv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class EncodersConfig {

    private final RsaKeyProperties rsaKeys;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), mustNotBeRefresh(), tokenVersionValid(), issuerValid(), audienceValid()
        ));
        return decoder;
    }

    @Bean
    @Qualifier("refreshTokenDecoder")
    JwtDecoder refreshTokenDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), mustBeRefresh(), tokenVersionValid(), issuerValid(), audienceValid()
        ));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> mustNotBeRefresh() {
        return jwt -> "refresh".equals(jwt.getClaimAsString("type"))
                ? OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Refresh token cannot authenticate requests", null))
                : OAuth2TokenValidatorResult.success();
    }

    private OAuth2TokenValidator<Jwt> mustBeRefresh() {
        return jwt -> "refresh".equals(jwt.getClaimAsString("type"))
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Expected a refresh token", null));
    }

    private OAuth2TokenValidator<Jwt> tokenVersionValid() {
        return jwt -> {
            Long claimVersion = jwt.getClaim("tokenVersion");
            if (claimVersion == null) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Missing tokenVersion claim", null));
            }
            return userRepository.findByEmail(jwt.getSubject())
                    .filter(user -> user.getTokenVersion() == claimVersion)
                    .map(user -> OAuth2TokenValidatorResult.success())
                    .orElseGet(() -> OAuth2TokenValidatorResult.failure(
                            new OAuth2Error("invalid_token", "Token revoked", null)));
        };
    }

    private OAuth2TokenValidator<Jwt> issuerValid() {
        return new JwtIssuerValidator(jwtProperties.getIssuer());
    }

    private OAuth2TokenValidator<Jwt> audienceValid() {
        return jwt -> jwt.getAudience().contains(jwtProperties.getAudience())
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Wrong audience", null));
    }
}

