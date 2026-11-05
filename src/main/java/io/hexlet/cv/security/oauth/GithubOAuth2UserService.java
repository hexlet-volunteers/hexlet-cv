package io.hexlet.cv.security.oauth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Профиль GitHub плюс основной подтверждённый email из {@code /user/emails}:
 * в {@code /user} приходит только публичный адрес, и про подтверждение там ничего нет.
 */
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    public static final String VERIFIED_EMAIL = "verified_email";

    private final RestClient restClient;

    public GithubOAuth2UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        var user = super.loadUser(request);
        Map<String, Object> attributes = new HashMap<>(user.getAttributes());
        var email = primaryVerifiedEmail(request.getAccessToken().getTokenValue());
        if (email != null) {
            attributes.put(VERIFIED_EMAIL, email);
        }
        return new DefaultOAuth2User(user.getAuthorities(), attributes, "id");
    }

    private String primaryVerifiedEmail(String accessToken) {
        try {
            List<Map<String, Object>> emails = restClient.get()
                    .uri("https://api.github.com/user/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() { });
            if (emails == null) {
                return null;
            }
            return emails.stream()
                    .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                    .map(e -> (String) e.get("email"))
                    .findFirst()
                    .orElse(null);
        } catch (RestClientException e) {
            return null;
        }
    }
}
