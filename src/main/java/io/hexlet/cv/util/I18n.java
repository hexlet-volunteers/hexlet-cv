package io.hexlet.cv.util;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class I18n {

    private final MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String get(String code, Object... params) {
        return messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }
}
