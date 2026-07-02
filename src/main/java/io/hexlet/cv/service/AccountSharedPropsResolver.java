package io.hexlet.cv.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountSharedPropsResolver {

    private final AccountMenuService accountMenuService;

    public Map<String, Object> resolve(String activeMenuKey) {
        return Map.of("account", Map.of(
                "menu", accountMenuService.getMenu(),
                "activeMenuKey", activeMenuKey
        ));
    }
}