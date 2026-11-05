package io.hexlet.cv.dto.api;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * Страница списка для REST API: элементы и данные для пагинации.
 *
 * @param items элементы текущей страницы
 * @param page номер страницы, начиная с 1
 * @param totalPages всего страниц
 * @param totalItems всего элементов
 * @param <T> тип элемента
 */
public record PageDTO<T>(List<T> items, int page, int totalPages, long totalItems) {

    public static <S, T> PageDTO<T> of(Page<S> page, Function<S, T> mapper) {
        return new PageDTO<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements());
    }
}
