package io.hexlet.cv.security;

import io.hexlet.cv.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    public Tokens authenticateAndGenerate(String email, String password) {
        var authentication = new UsernamePasswordAuthenticationToken(email, password);
        // authenticationManager.authenticate(authentication);
        
        var result = authenticationManager.authenticate(authentication);
        /*
        var roles = result.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
        */

        var accessToken = jwtUtils.generateAccessToken(email);
        var refreshToken = jwtUtils.generateRefreshToken(email);

        return new Tokens(accessToken, refreshToken);
    }

    public record Tokens(String access, String refresh) {

    }
}
