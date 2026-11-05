package io.hexlet.cv.security.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.security.oauth.GithubAccountService.GithubProfile;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GithubAccountServiceTest {

    private static final List<String> EMAILS = List.of(
            "gh-legacy@example.com", "gh-password@example.com", "gh-new@example.com", "gh-other@example.com");

    @Autowired
    private GithubAccountService service;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        EMAILS.forEach(e -> userRepository.findByEmail(e).ifPresent(userRepository::delete));
    }

    @Test
    void findsLegacyUserByProviderAndUid() {
        var legacy = user("gh-legacy@example.com", "github", "12345");

        var result = service.resolve(new GithubProfile("12345", "octo", "Octo Cat", "changed@example.com"));

        assertThat(result.user()).get().extracting(User::getId).isEqualTo(legacy.getId());
    }

    @Test
    void linksPasswordAccountByVerifiedEmail() {
        var existing = user("gh-password@example.com", null, null);

        var result = service.resolve(new GithubProfile("777", "octo", "Octo", "GH-Password@example.com"));

        assertThat(result.user()).get().extracting(User::getId).isEqualTo(existing.getId());
        var saved = userRepository.findById(existing.getId()).orElseThrow();
        assertThat(saved.getProvider()).isEqualTo("github");
        assertThat(saved.getUid()).isEqualTo("777");
    }

    @Test
    void createsNewUserWithNameSplit() {
        var result = service.resolve(new GithubProfile("888", "octo", "Анна Смирнова", "gh-new@example.com"));

        var user = result.user().orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Анна");
        assertThat(user.getLastName()).isEqualTo("Смирнова");
        assertThat(user.getRole()).isEqualTo(RoleType.CANDIDATE);
        assertThat(user.getEncryptedPassword()).isNull();
    }

    @Test
    void refusesWithoutVerifiedEmail() {
        var result = service.resolve(new GithubProfile("999", "octo", null, null));

        assertThat(result.user()).isEmpty();
        assertThat(result.error()).isEqualTo("github_no_email");
    }

    @Test
    void refusesEmailLinkedToAnotherGithubAccount() {
        user("gh-other@example.com", "github", "111");

        var result = service.resolve(new GithubProfile("222", "intruder", null, "gh-other@example.com"));

        assertThat(result.user()).isEmpty();
        assertThat(result.error()).isEqualTo("github_conflict");
    }

    private User user(String email, String provider, String uid) {
        var u = new User();
        u.setEmail(email);
        u.setFirstName("Имя");
        u.setLastName("Фамилия");
        u.setRole(RoleType.CANDIDATE);
        u.setProvider(provider);
        u.setUid(uid);
        return userRepository.save(u);
    }
}
