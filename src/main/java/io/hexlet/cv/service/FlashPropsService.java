package io.hexlet.cv.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

@Service
public class FlashPropsService {

    public Map<String, Object> buildProps(HttpServletRequest request) {
        Map<String, Object> props = new HashMap<>();

        // 1. Получаем flash-сообщения
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (flash != null && !flash.isEmpty()) {
            props.put("flash", flash);
        }

        // 2. Добавляем локаль из контекста Spring, если она нужна во фронтенде
        String currentLocale = org.springframework.context.i18n.LocaleContextHolder.getLocale().getLanguage();
        props.put("locale", currentLocale);

        return props;
    }
}
