package io.hexlet.cv.dto.application;

import io.hexlet.cv.model.enums.ApplicationEventType;
import io.hexlet.cv.model.enums.ApplicationStage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Ответы API трекера откликов (#1155).
 */
public final class ApplicationDTOs {

    private ApplicationDTOs() {
    }

    // Версия резюме, ушедшая в отклик.
    public record ResumeRef(Long id, String name) {
    }

    // Вычисленная подсказка: SILENCE — дней без событий, DEADLINE — дней до дедлайна.
    public record Alert(String type, long days) {
    }

    // Карточка на доске и строка таблицы.
    public record Summary(
            Long id,
            String company,
            String position,
            String salary,
            String source,
            ApplicationStage stage,
            ApplicationStage rejectedAtStage,
            ResumeRef resume,
            LocalDate deadline,
            String nextStepTitle,
            LocalDateTime nextStepAt,
            LocalDateTime lastEventAt,
            LocalDateTime createdAt,
            List<Alert> alerts) {
    }

    public record Event(Long id, ApplicationEventType type, String text, LocalDateTime occurredAt) {
    }

    // Отклик целиком — для боковой панели.
    public record Details(
            Long id,
            String company,
            String position,
            String salary,
            String vacancyUrl,
            String source,
            ApplicationStage stage,
            ApplicationStage rejectedAtStage,
            ResumeRef resume,
            LocalDate deadline,
            String nextStepTitle,
            LocalDateTime nextStepAt,
            LocalDateTime lastEventAt,
            LocalDateTime createdAt,
            List<Alert> alerts,
            List<Event> events) {
    }

    // Отказы: всего и на каких этапах получены.
    public record Rejections(long total, Map<ApplicationStage, Long> byStage) {
    }

    // Список с счётчиками по стадиям: ключ ALL — все с учётом поиска и фильтра по резюме.
    public record ListResponse(List<Summary> items, Map<String, Long> counts, Rejections rejections) {
    }
}
