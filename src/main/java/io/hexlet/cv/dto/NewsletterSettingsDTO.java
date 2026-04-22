package io.hexlet.cv.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSettingsDTO {

    @NotNull(message = "{newsletter.settings.new_courses.required}")
        private Boolean newCourses;

    @NotNull(message = "{newsletter.settings.course_updates.required}")
        private Boolean courseUpdates;

    @NotNull(message = "{newsletter.settings.promotions.required}")
        private Boolean promotions;

    @NotNull(message = "{newsletter.settings.achievements.required}")
        private Boolean achievements;

    @NotNull(message = "{newsletter.settings.comments_replies.required}")
        private Boolean commentsReplies;

    @NotNull(message = "{newsletter.settings.resume_views.required}")
        private Boolean resumeViews;

    @NotNull(message = "{newsletter.settings.vacancy_matches.required}")
        private Boolean vacancyMatches;

    @NotNull(message = "{newsletter.settings.community_news.required}")
        private Boolean communityNews;

    @NotNull(message = "{newsletter.settings.marketing_tips.required}")
        private Boolean marketingTips;

}
