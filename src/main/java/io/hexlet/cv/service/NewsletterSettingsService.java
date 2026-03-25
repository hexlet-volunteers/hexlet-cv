package io.hexlet.cv.service;

import io.hexlet.cv.dto.NewsletterSettingsDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.model.NewsletterSettings;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.NewsletterSettingsRepository;
import io.hexlet.cv.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsletterSettingsService {

    private final NewsletterSettingsRepository newsletterSettingsRepository;
    private final UserUtils userUtils;

    @Transactional(readOnly = true)
    public NewsletterSettingsDTO getCurrentUserSettings() {
        User currentUser = getCurrentUser();
        NewsletterSettings settings = getOrCreateSettings(currentUser);
        return convertToDto(settings);
    }

    @Transactional
    public NewsletterSettingsDTO updateCurrentUserSettings(NewsletterSettingsDTO settingsDto) {
        validateSettings(settingsDto);

        User currentUser = getCurrentUser();
        NewsletterSettings settings = getOrCreateSettings(currentUser);

        updateSettingsFromDto(settings, settingsDto);

        NewsletterSettings savedSettings = newsletterSettingsRepository.save(settings);
        return convertToDto(savedSettings);
    }

    private void validateSettings(NewsletterSettingsDTO settingsDto) {
        if (settingsDto == null) {
            throw new IllegalArgumentException("Settings DTO cannot be null");
        }
    }

    private void updateSettingsFromDto(NewsletterSettings settings, NewsletterSettingsDTO dto) {
        settings.setNewCourses(dto.getNewCourses());
        settings.setCourseUpdates(dto.getCourseUpdates());
        settings.setPromotions(dto.getPromotions());
        settings.setAchievements(dto.getAchievements());
        settings.setCommentsReplies(dto.getCommentsReplies());
        settings.setResumeViews(dto.getResumeViews());
        settings.setVacancyMatches(dto.getVacancyMatches());
        settings.setCommunityNews(dto.getCommunityNews());
        settings.setMarketingTips(dto.getMarketingTips());
    }

    private User getCurrentUser() {
        User currentUser = userUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ClientException("User not authenticated");
        }
        return currentUser;
    }

    private NewsletterSettings getOrCreateSettings(User user) {
        return newsletterSettingsRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    NewsletterSettings newSettings = NewsletterSettings.builder()
                            .user(user)
                            .build();
                    return newsletterSettingsRepository.save(newSettings);
                });
    }

    private NewsletterSettingsDTO convertToDto(NewsletterSettings settings) {
        return NewsletterSettingsDTO.builder()
                .newCourses(settings.getNewCourses())
                .courseUpdates(settings.getCourseUpdates())
                .promotions(settings.getPromotions())
                .achievements(settings.getAchievements())
                .commentsReplies(settings.getCommentsReplies())
                .resumeViews(settings.getResumeViews())
                .vacancyMatches(settings.getVacancyMatches())
                .communityNews(settings.getCommunityNews())
                .marketingTips(settings.getMarketingTips())
                .build();
    }

}
