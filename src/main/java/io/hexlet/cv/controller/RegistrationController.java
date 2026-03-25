
package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
public class RegistrationController {

    private final Inertia inertia;
    private final RegistrationService userService;
    private final TokenService tokenService;
    // private final AuthResponseService authResponseService;
    private final FlashPropsService flashPropsService;

    private final TokenCookieService tokenCookieService;

    @GetMapping("/users/sign_up")
    public ResponseEntity<?> signUpForm(HttpServletRequest request) {

        var props = flashPropsService.buildProps(request);

        return inertia.render("Users/SignUp", props);
    }

    @PostMapping(path = "/users")
    public Object registration(@Valid @RequestBody RegistrationRequestDTO inputDTO,
                               HttpServletResponse response,
                               HttpSession session) {

        userService.registration(inputDTO);

        var tokens = tokenService.authenticateAndGenerate(
                inputDTO.getEmail(),
                inputDTO.getPassword()
        );

        var access = tokenCookieService.buildAccessCookie(tokens.access());
        var refresh = tokenCookieService.buildRefreshCookie(tokens.refresh());

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        session.setAttribute("flash", Map.of("success", true));

        return inertia.redirect("/dashboard");


// ----------
        // return authResponseService.success(locale, tokens, response);
    }
}
