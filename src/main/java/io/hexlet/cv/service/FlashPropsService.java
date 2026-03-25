package io.hexlet.cv.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

@Service
public class FlashPropsService {

    // НОВЫЙ МЕТОД: без передачи локали вручную
    public Map<String, Object> buildProps(HttpServletRequest request) {
        // Просто вызываем старый метод, подставляя локаль из контекста Spring
        String currentLocale = org.springframework.context.i18n.LocaleContextHolder.getLocale().getLanguage();
        return buildProps(currentLocale, request);
    }

    public Map<String, Object> buildProps(String locale, HttpServletRequest request) {
        Map<String, Object> props = new HashMap<>();

        var flash = RequestContextUtils.getInputFlashMap(request);

        if (flash != null && !flash.isEmpty()) {
            props.put("flash", flash);
        }

        props.put("locale", locale);

        return props;
    }
}
