package io.hexlet.cv.security.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/**
 * Регистрация OAuth-приложения GitHub. Включается, только если задан
 * {@code app.oauth.github.client-id} (переменная {@code GITHUB_CLIENT_ID}):
 * без ключей приложение стартует как раньше, а кнопка входа через GitHub отвечает 404.
 */
@Configuration
@ConditionalOnExpression("!'${app.oauth.github.client-id:}'.isBlank()")
public class GithubOAuthConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${app.oauth.github.client-id}") String clientId,
            @Value("${app.oauth.github.client-secret}") String clientSecret) {
        var github = CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId(clientId)
                .clientSecret(clientSecret)
                // user:email — чтобы получить подтверждённый email, даже если в профиле он скрыт
                .scope("read:user", "user:email")
                .build();
        return new InMemoryClientRegistrationRepository(github);
    }
}
