package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.user.auth.LoginRequestDTO;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.LoginService;
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
public class LoginController {

    private final Inertia inertia;
    private final LoginService loginService;
    private final TokenService tokenService;
    //   private final AuthResponseService authResponseService;
    private final FlashPropsService flashPropsService;


    private final TokenCookieService tokenCookieService;

    @GetMapping("/users/sign_in")
    public ResponseEntity<?> signInForm(HttpServletRequest request) {

        var props = flashPropsService.buildProps(request);

        return inertia.render("Users/SignIn", props);
    }

    @PostMapping(path = "/users/sign_in")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginDTO,
                                   HttpServletResponse response,
                                   HttpSession session) {

        loginService.login(loginDTO);
        var tokens = tokenService.authenticateAndGenerate(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        );

        var access = tokenCookieService.buildAccessCookie(tokens.access());
        var refresh = tokenCookieService.buildRefreshCookie(tokens.refresh());

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        session.setAttribute("flash", Map.of("success", true));

        return inertia.redirect("/dashboard");

        //  return authResponseService.success(locale, tokens, response);
    }
}
