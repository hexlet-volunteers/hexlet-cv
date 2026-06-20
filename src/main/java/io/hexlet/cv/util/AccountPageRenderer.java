package io.hexlet.cv.util;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.service.AccountSharedPropsResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountPageRenderer {

    private final Inertia inertia;
    private final AccountSharedPropsResolver accountSharedPropsResolver;

    public ResponseEntity<String> render(String component, String activeMenuKey, Map<String, Object> pageProps) {
        Map<String, Object> sharedProps = accountSharedPropsResolver.resolve(activeMenuKey);
        Map<String, Object> allProps = new HashMap<>();
        allProps.putAll(sharedProps);
        allProps.putAll(pageProps);
        return inertia.render(component, allProps);
    }
}
