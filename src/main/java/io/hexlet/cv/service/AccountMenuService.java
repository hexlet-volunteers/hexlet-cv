package io.hexlet.cv.service;

import io.hexlet.cv.dto.account.MenuItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountMenuService {
    public List<MenuItemDTO> getMenu() {
        return List.of(
                new MenuItemDTO("Мое обучение", "/account/my-progress"),
                new MenuItemDTO("Покупки и подписки", "/account/purchase"),
                new MenuItemDTO("Вебинары", "/account/webinars"),
                new MenuItemDTO("База знаний", "/account/knowledge"),
                new MenuItemDTO("Интервью", null),
                new MenuItemDTO("Грейдирование", null),
                new MenuItemDTO("Программы обучения", "/account/programs"),
                new MenuItemDTO("Резюме", null),
                new MenuItemDTO("Сопроводительное", null),
                new MenuItemDTO("Автоотклики", null),
                new MenuItemDTO("Избранное", null),
                new MenuItemDTO("Уведомления", "/account/notifications"),
                new MenuItemDTO("Поддержка", null),
                new MenuItemDTO("Настройки", null)
        );
    }
}
