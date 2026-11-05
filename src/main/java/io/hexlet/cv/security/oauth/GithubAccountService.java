package io.hexlet.cv.security.oauth;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.UserRepository;
import java.util.Locale;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Находит или создаёт пользователя по данным GitHub.
 *
 * <p>Порядок важен для переезда со старого сайта: в Rails вход через GitHub записывал
 * {@code provider = 'github'} и числовой {@code uid}, поэтому сначала ищем по ним.
 * Затем — по подтверждённому на GitHub email (так привязываются аккаунты с паролем).
 * Неподтверждённый email для привязки не используем: иначе можно войти в чужой аккаунт,
 * указав на GitHub его адрес.
 */
@Slf4j
@Service
@AllArgsConstructor
public class GithubAccountService {

    public static final String PROVIDER = "github";

    private final UserRepository userRepository;

    /**
     * Профиль GitHub, нужный для входа.
     *
     * @param uid числовой id пользователя на GitHub
     * @param login логин
     * @param name отображаемое имя, может быть пустым
     * @param verifiedEmail основной подтверждённый email, может отсутствовать
     */
    public record GithubProfile(String uid, String login, String name, String verifiedEmail) {
    }

    /**
     * Результат: пользователь или причина отказа.
     *
     * @param user найденный или созданный пользователь
     * @param error код ошибки для страницы входа
     */
    public record Result(Optional<User> user, String error) {
        static Result ok(User user) {
            return new Result(Optional.of(user), null);
        }

        static Result fail(String error) {
            return new Result(Optional.empty(), error);
        }
    }

    @Transactional
    public Result resolve(GithubProfile profile) {
        var byUid = userRepository.findByProviderAndUid(PROVIDER, profile.uid());
        if (byUid.isPresent()) {
            return Result.ok(byUid.get());
        }

        if (profile.verifiedEmail() == null) {
            return Result.fail("github_no_email");
        }
        var email = profile.verifiedEmail().toLowerCase(Locale.ROOT);

        var byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            var user = byEmail.get();
            if (user.getProvider() != null && !PROVIDER.equals(user.getProvider())) {
                return Result.fail("github_conflict");
            }
            if (user.getUid() != null && !user.getUid().equals(profile.uid())) {
                // email уже привязан к другому аккаунту GitHub
                return Result.fail("github_conflict");
            }
            user.setProvider(PROVIDER);
            user.setUid(profile.uid());
            log.info("Аккаунт привязан к GitHub по подтверждённому email (user id {})", user.getId());
            return Result.ok(userRepository.save(user));
        }

        var user = new User();
        user.setEmail(email);
        user.setProvider(PROVIDER);
        user.setUid(profile.uid());
        user.setRole(RoleType.CANDIDATE);
        var parts = profile.name() == null || profile.name().isBlank()
                ? new String[] {profile.login()}
                : profile.name().trim().split("\\s+", 2);
        user.setFirstName(parts[0]);
        user.setLastName(parts.length > 1 ? parts[1] : "");
        return Result.ok(userRepository.save(user));
    }
}
