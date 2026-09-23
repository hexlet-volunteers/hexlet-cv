package io.hexlet.cv.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.AuditLogger;
import io.hexlet.cv.audit.AuditReason;
import io.hexlet.cv.audit.AuditSubject;
import io.hexlet.cv.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http,
                                 JwtDecoder jwtDecoder,
                                 BearerTokenResolver cookieTokenResolver,
                                 Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter,
                                 AuthenticationEntryPoint jsonAuthEntryPoint,
                                 AccessDeniedHandler auditingAccessDeniedHandler)
            throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jsonAuthEntryPoint)
                        .accessDeniedHandler(auditingAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AdminPaths.adminZonePatterns()).hasRole("ADMIN")
                        .requestMatchers("/account/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(rs -> rs
                        .bearerTokenResolver(cookieTokenResolver)
                        .authenticationEntryPoint(jsonAuthEntryPoint)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        ))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                        .permissionsPolicyHeader(permissions -> permissions
                                .policy("geolocation=(), camera=(), microphone=()"))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; "
                                                + "script-src 'self'; "
                                                + "style-src 'self' 'unsafe-inline'; "
                                                + "img-src 'self' data:; "
                                                + "font-src 'self'; "
                                                + "connect-src 'self'; "
                                                + "frame-ancestors 'none'; "
                                                + "base-uri 'self'; "
                                                + "form-action 'self'; "
                                                + "object-src 'none'"
                                )
                        ));
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint jsonAuthEntryPoint(ObjectMapper om, AuditLogger auditLogger) {
        return (request, response, authException) -> {
            auditLogger.logFailure(AuditEventType.UNAUTHORIZED, AuditSubject.current(),
                    resolveUnauthorizedReason(authException), request);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            var body = Map.of("errors", Map.of("auth", "Authentication required or token invalid"));
            response.getWriter().write(om.writeValueAsString(body));
        };
    }

    /**
     * Различает два повода для 401. Предъявленный и отвергнутый токен - след атаки или
     * испорченной куки, отсутствующий токен - обычный незалогиненный запрос.
     *
     * @param authException исключение, с которым сработала точка входа
     * @return причина отказа для записи в журнал
     */
    private static AuditReason resolveUnauthorizedReason(AuthenticationException authException) {
        return authException instanceof OAuth2AuthenticationException
                ? AuditReason.TOKEN_INVALID
                : AuditReason.TOKEN_MISSING;
    }

    @Bean
    public AccessDeniedHandler auditingAccessDeniedHandler(ObjectMapper om, AuditLogger auditLogger) {
        return (request, response, accessDeniedException) -> {
            auditLogger.logFailure(AuditEventType.ACCESS_DENIED, AuditSubject.current(),
                    AuditReason.FORBIDDEN, request);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            var body = Map.of("errors", Map.of("Access denied error", "Access is denied"));
            response.getWriter().write(om.writeValueAsString(body));
        };
    }

    @Bean
    @Profile("dev")
    public CorsConfigurationSource corsConfigurationSourceDev() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "X-Inertia",
                "X-Inertia-Version",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    @Profile("prod")
    public CorsConfigurationSource corsConfigurationSourceProd() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
                "https://cv.hexlet.io"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "X-Inertia",
                "X-Inertia-Version",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(CustomUserDetailsService userService,
                                                     PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthProvider) {
        return new ProviderManager(daoAuthProvider);
    }

    @Bean
    public BearerTokenResolver cookieTokenResolver() {
        return request -> {
            var cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }
            for (var c : cookies) {
                if ("access_token".equals(c.getName())) {
                    return c.getValue();
                }
            }
            return null;
        };
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter() {
        return jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            addRoles(authorities, jwt.getClaim("roles"));

            Map<String, Object> realm = jwt.getClaim("realm_access");
            if (realm != null) {
                addRoles(authorities, realm.get("roles"));
            }

            addRoles(authorities, jwt.getClaim("authorities"));

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }

    private static void addRoles(Set<GrantedAuthority> out, Object claim) {
        if (claim instanceof Collection<?> col) {
            for (Object v : col) {
                String role = String.valueOf(v);
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                out.add(new SimpleGrantedAuthority(role));
            }
        }
    }
}
