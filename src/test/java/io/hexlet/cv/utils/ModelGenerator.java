package io.hexlet.cv.utils;

import io.hexlet.cv.model.NewsletterSettings;
import io.hexlet.cv.model.PageSection;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;
    private Model<PageSection> pageSectionModel;
    private Model<NewsletterSettings> newsletterSettingsModel;

    private static final Faker FAKER = new Faker();

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
            .ignore(Select.field(User::getId))
            .supply(Select.field(User::getEmail), () -> FAKER.internet().emailAddress())
            .supply(Select.field(User::getFirstName), () -> FAKER.name().firstName())
            .supply(Select.field(User::getLastName), () -> FAKER.name().lastName())
            .supply(Select.field(User::getEncryptedPassword), () -> FAKER.internet().password(3, 64))
            .supply(Select.field(User::getRole), () -> RoleType.CANDIDATE) // Роль по умолчанию
            .set(Select.field(User::getNewsletterSettings), null)
            .toModel();

        pageSectionModel = Instancio.of(PageSection.class)
            .ignore(Select.field(PageSection::getId))
            .ignore(Select.field(PageSection::getPageKey)) // Техническое название страницы указывается в тестах
            .supply(Select.field(PageSection::getSectionKey), () -> FAKER.internet().slug())
            .supply(Select.field(PageSection::getTitle), () -> FAKER.name().title())
            .supply(Select.field(PageSection::getContent), () -> FAKER.lorem().characters(0, 200))
            .supply(Select.field(PageSection::isActive), () -> true) // Включённость по умолчанию
            .toModel();

        newsletterSettingsModel = Instancio.of(NewsletterSettings.class)
                .ignore(Select.field(NewsletterSettings::getId))
                .ignore(Select.field(NewsletterSettings::getUser)) // User задается отдельно
                .supply(Select.field(NewsletterSettings::getNewCourses), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getCourseUpdates), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getPromotions), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getAchievements), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getCommentsReplies), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getResumeViews), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getVacancyMatches), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getCommunityNews), () -> FAKER.bool().bool())
                .supply(Select.field(NewsletterSettings::getMarketingTips), () -> FAKER.bool().bool())
                .toModel();
    }

    public User createUserWithSettings() {
        User user = Instancio.create(userModel);

        NewsletterSettings settings = Instancio.create(newsletterSettingsModel);
        settings.setUser(user);
        user.setNewsletterSettings(settings);

        return user;
    }

    // Метод для создания пользователя с определенными настройками
    public User createUserWithCustomSettings(NewsletterSettings customSettings) {
        User user = Instancio.create(userModel);
        customSettings.setUser(user);
        user.setNewsletterSettings(customSettings);
        return user;
    }
}
