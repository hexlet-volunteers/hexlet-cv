package io.hexlet.cv.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.hexlet.cv.dto.account.MenuItemDTO;
import io.hexlet.cv.service.AccountMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerUtils {

    public static final String ACTIVE_MAIN_SECTION = "activeMainSection";
    public static final String ACTIVE_SUB_SECTION = "activeSubSection";
    public static final String MAIN_SECTION_MARKETING = "marketing";
    public static final String MAIN_SECTION_ACCOUNT = "account";

    private final AccountMenuService accountMenuService;

    public Map<String, Object> createBaseProps(String mainSection, String subSection) {
        return Map.of(
                ACTIVE_MAIN_SECTION, mainSection,
                ACTIVE_SUB_SECTION, subSection
        );
    }

    public void addPropertyIfPresent(Map<String, Object> props, String key, String value) {
        if (StringUtils.hasText(value)) {
            props.put(key, value);
        }
    }

    public Map<String, Object> createMarketingProps(String subSection) {
        return createBaseProps(MAIN_SECTION_MARKETING, subSection);
    }

    public Map<String, Object> createAccountProps(String subSection) {
        List<MenuItemDTO> menu = accountMenuService.getMenu();
        Map<String, Object> props = new HashMap<>();
        props.put(ACTIVE_MAIN_SECTION, MAIN_SECTION_ACCOUNT);
        props.put(ACTIVE_SUB_SECTION, subSection);
        props.put("menu", menu);
        return props;
    }

    public Map<String, Object> createPaginationMap(Page<?> page, Pageable pageable) {
        return Map.of(
                "currentPage", page.getNumber(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements(),
                "pageSize", pageable.getPageSize()
        );
    }

    private String getDefaultContentKey(String subSection) {
        Map<String, String> mapping = Map.of(
                "articles", "articles",
                "stories", "stories",
                "reviews", "reviews",
                "team", "team",
                "pricing", "pricing",
                "knowledge-articles", "articles",
                "knowledge-interviews", "interviews"
        );

        return mapping.getOrDefault(subSection, "content");
    }
}
