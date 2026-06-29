package io.hexlet.cv.controller.admin.marketing;

import io.hexlet.cv.controller.admin.marketing.support.AdminMarketingControllerTestSupport;
import io.hexlet.cv.dto.marketing.TeamCreateDto;
import io.hexlet.cv.dto.marketing.TeamUpdateDto;
import io.hexlet.cv.model.enums.TeamMemberType;
import io.hexlet.cv.model.enums.TeamPosition;
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
 * Создание и обновление участников команды через {@code POST/PUT /admin/marketing/team}:
 * успешные запросы и валидация DTO (firstName, position, memberType).
 */
public class AdminMarketingTeamControllerTest extends AdminMarketingControllerTestSupport {

    @Test
    void shouldCreateTeamMemberAndRedirect() throws Exception {
        var dto = new TeamCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPosition(TeamPosition.MARKETING);
        dto.setMemberType(TeamMemberType.MENTOR);
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_TEAM)));

        ArgumentCaptor<TeamCreateDto> captor = ArgumentCaptor.forClass(TeamCreateDto.class);
        verify(teamService()).createTeamMember(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("John");
        assertThat(captor.getValue().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldRejectTeamMemberWithBlankFirstName() throws Exception {
        var dto = new TeamCreateDto();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setPosition(TeamPosition.MARKETING);
        dto.setMemberType(TeamMemberType.MENTOR);
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void shouldRejectTeamMemberWithMissingPosition() throws Exception {
        var dto = new TeamCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMemberType(TeamMemberType.MENTOR);
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.position").exists());
    }

    @Test
    void shouldRejectTeamMemberWithMissingMemberType() throws Exception {
        var dto = new TeamCreateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPosition(TeamPosition.MARKETING);
        dto.setShowOnHomepage(false);

        mockMvc().perform(post(sectionPath(SECTION_TEAM))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.memberType").exists());
    }

    @Test
    void shouldUpdateTeamMemberAndRedirect() throws Exception {
        var dto = new TeamUpdateDto();
        dto.setFirstName(JsonNullable.of("Jane"));

        mockMvc().perform(put(sectionPath(SECTION_TEAM) + "/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper().writeValueAsString(dto))
                        .with(user(ADMIN).roles(ROLE_ADMIN)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(REDIRECT_HEADER, sectionPath(SECTION_TEAM)));

        verify(teamService()).updateTeamMember(eq(4L), any(TeamUpdateDto.class));
    }
}
