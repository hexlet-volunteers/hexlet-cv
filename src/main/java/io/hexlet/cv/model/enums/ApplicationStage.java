package io.hexlet.cv.model.enums;

/**
 * Стадия отклика в воронке «отклик → оффер». {@link #REJECTED} — терминальная,
 * стадия, на которой пришёл отказ, хранится отдельно.
 */
public enum ApplicationStage {
    SAVED,
    APPLIED,
    SCREENING,
    INTERVIEW,
    OFFER,
    REJECTED
}
