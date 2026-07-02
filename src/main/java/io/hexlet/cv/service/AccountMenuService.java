package io.hexlet.cv.service;

import io.hexlet.cv.dto.account.MenuItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountMenuService {
    public List<MenuItemDTO> getMenu() {
        return List.of(
                new MenuItemDTO("Мое обучение", "/account/my-progress", "myProgress"),
                new MenuItemDTO("Покупки и подписки", "/account/purchase", "purchase"),
                new MenuItemDTO("Вебинары", "/account/webinars", "webinars"),
                new MenuItemDTO("База знаний", "/account/knowledge", "knowledge"),
                new MenuItemDTO("Интервью", "/account/knowledge/interviews", "knowledgeInterviews"),
                new MenuItemDTO("Грейдирование", null, null),
                new MenuItemDTO("Программы обучения", "/account/programs", "programs"),
                new MenuItemDTO("Резюме", null, null),
                new MenuItemDTO("Сопроводительное", null, null),
                new MenuItemDTO("Автоотклики", null, null),
                new MenuItemDTO("Избранное", null, null),
                new MenuItemDTO("Уведомления", "/account/notifications", "notifications"),
                new MenuItemDTO("Поддержка", null, null),
                new MenuItemDTO("Настройки", null, null)
        );
    }
}
