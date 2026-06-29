package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import io.hexlet.cv.dto.marketing.PricingCreateDto;
import io.hexlet.cv.dto.marketing.PricingUpdateDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Создание и обновление тарифов через {@code POST/PUT /admin/marketing/pricing}:
 * успешные запросы и валидация DTO (name, originalPrice, discountPercent).
 */
public class AdminMarketingPricingControllerTest extends AdminMarketingControllerTestSupport {

    @Test
    void shouldCreatePricingAndRedirect() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("Basic");
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(10.0);
        dto.setDescription("Basic plan description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_PRICING)));

        ArgumentCaptor<PricingCreateDto> captor = ArgumentCaptor.forClass(PricingCreateDto.class);
        verify(pricingPlanService()).createPricing(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Basic");
        assertThat(captor.getValue().getOriginalPrice()).isEqualTo(99.0);
    }

    @Test
    void shouldRejectPricingWithBlankName() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("");
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(0.0);
        dto.setDescription("description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void shouldRejectPricingWithZeroPrice() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("Plan");
        dto.setOriginalPrice(0.0);
        dto.setDiscountPercent(0.0);
        dto.setDescription("description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.originalPrice").exists());
    }

    @Test
    void shouldRejectPricingWithDiscountOver100() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("Plan");
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(150.0);
        dto.setDescription("description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.discountPercent").exists());
    }

    @Test
    void shouldRejectPricingWithNegativeDiscount() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("Plan");
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(-5.0);
        dto.setDescription("description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.discountPercent").exists());
    }

    @Test
    void shouldRejectPricingWithNameExceeding100Chars() throws Exception {
        var dto = new PricingCreateDto();
        dto.setName("A".repeat(101));
        dto.setOriginalPrice(99.0);
        dto.setDiscountPercent(0.0);
        dto.setDescription("description");

        mockMvc().perform(post(sectionPath(SECTION_PRICING))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void shouldUpdatePricingAndRedirect() throws Exception {
        var dto = new PricingUpdateDto();
        dto.setName(JsonNullable.of("Pro"));
        dto.setOriginalPrice(JsonNullable.of(199.0));

        mockMvc().perform(put(sectionPath(SECTION_PRICING) + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_PRICING)));

        verify(pricingPlanService()).updatePricing(eq(5L), any(PricingUpdateDto.class));
    }

    @Test
    void shouldRejectPricingUpdateWithInvalidDiscount() throws Exception {
        var dto = new PricingUpdateDto();
        dto.setDiscountPercent(JsonNullable.of(150.0));

        mockMvc().perform(put(sectionPath(SECTION_PRICING) + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.discountPercent").exists());
    }
}
