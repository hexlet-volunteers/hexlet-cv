package io.hexlet.cv.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.cv.dto.pagesection.PageSectionCreateDTO;
import io.hexlet.cv.dto.pagesection.PageSectionUpdateDTO;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.PageSectionRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import io.hexlet.cv.utils.ModelGenerator;
import jakarta.servlet.http.Cookie;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PageSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PageSectionRepository pageSectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private static final String ADMIN_EMAIL = "page_section_admin@example.com";
    private static final String CANDIDATE_EMAIL = "page_section_candidate@example.com";

    private String givenAdminAccessToken() {
        userRepository.save(User.builder()
                .email(ADMIN_EMAIL)
                .encryptedPassword(encoder.encode("password"))
                .role(RoleType.ADMIN)
                .build());
        return jwtUtils.generateAccessToken(ADMIN_EMAIL);
    }

    private static PageSectionCreateDTO arbitraryPageSectionCreateDto() {
        var dto = new PageSectionCreateDTO();
        dto.setPageKey("somePageKey");
        dto.setSectionKey("someSectionKey");
        return dto;
    }

    @Test
    public void testGetAll() throws Exception {

        // given
        var section1Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section1Draft.setPageKey("main");
        var section1 = pageSectionRepository.save(section1Draft);

        var section2Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section2Draft.setPageKey("profile");
        section2Draft.setActive(false);
        var section2 = pageSectionRepository.save(section2Draft);

        var adminToken = givenAdminAccessToken();

        // when
        var result = mockMvc.perform(get("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"));

        // then
        var response = result.andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getContentAsString())
            .contains(section1.getPageKey())
            .contains(section2.getPageKey())
            .contains(section1.getSectionKey())
            .contains(section2.getSectionKey());
    }

    @Test
    public void testGetAllWithParams() throws Exception {

        // given
        var section1Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section1Draft.setPageKey("main");
        var section1 = pageSectionRepository.save(section1Draft);

        var section2Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section2Draft.setPageKey("profile");
        section2Draft.setActive(false);
        var section2 = pageSectionRepository.save(section2Draft);

        var section3Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section3Draft.setPageKey(section1.getPageKey());
        section3Draft.setActive(section2.isActive());
        var section3 = pageSectionRepository.save(section3Draft);

        var adminToken = givenAdminAccessToken();

        // when
        var result = mockMvc.perform(get("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .param("page", section1.getPageKey())
                .param("active", String.valueOf(section2.isActive())));

        // then
        var response = result.andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getContentAsString())
            .doesNotContain(section1.getSectionKey())
            .doesNotContain(section2.getSectionKey())
            .contains(section3.getSectionKey());
    }

    @Test
    public void testGet() throws Exception {

        // given
        var section1Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section1Draft.setPageKey("main");
        var section1 = pageSectionRepository.save(section1Draft);

        var adminToken = givenAdminAccessToken();

        // when
        var result = mockMvc.perform(get("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"));

        // then
        var response = result.andExpect(status().isOk()).andReturn().getResponse();
        assertThatJson(response.getContentAsString()).and(
            v -> v.node("props.pageSections[0].id").isEqualTo(section1.getId()),
            v -> v.node("props.pageSections[0].pageKey").isEqualTo(section1.getPageKey()),
            v -> v.node("props.pageSections[0].sectionKey").isEqualTo(section1.getSectionKey())
        );
    }

    @Test
    public void testCreate() throws Exception {

        // given
        var adminToken = givenAdminAccessToken();

        var template = Instancio.of(modelGenerator.getPageSectionModel()).create();
        template.setPageKey("main");

        var dto = new PageSectionCreateDTO();
        dto.setPageKey(template.getPageKey());
        dto.setSectionKey(template.getSectionKey());
        dto.setTitle(template.getTitle());
        dto.setContent(template.getContent());
        dto.setActive(template.isActive());

        // when
        var result = mockMvc.perform(post("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isFound());
        var section = assertDoesNotThrow(() ->
            pageSectionRepository.findBySectionKey(dto.getSectionKey()).orElseThrow());
        assertThat(section.getPageKey()).isEqualTo(dto.getPageKey());
        assertThat(section.getTitle()).isEqualTo(dto.getTitle());
        assertThat(section.getContent()).isEqualTo(dto.getContent());
        assertThat(section.isActive()).isEqualTo(dto.getActive());
    }

    @Test
    public void testUpdate() throws Exception {

        // given
        var section1Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section1Draft.setPageKey("main");
        var section1 = pageSectionRepository.save(section1Draft);

        var adminToken = givenAdminAccessToken();

        var dto = new PageSectionUpdateDTO();
        dto.setPageKey(JsonNullable.of("profile"));
        dto.setSectionKey(JsonNullable.of("tech_stack"));
        dto.setActive(JsonNullable.of(false));
        dto.setTitle(JsonNullable.undefined());
        dto.setContent(JsonNullable.undefined());
        var oldTitle = section1.getTitle();
        var oldContent = section1.getContent();

        // when
        var result = mockMvc.perform(put("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isSeeOther());
        var section = assertDoesNotThrow(() ->
            pageSectionRepository.findById(section1.getId()).orElseThrow());
        assertThat(section.getPageKey()).isEqualTo(dto.getPageKey().get());
        assertThat(section.getSectionKey()).isEqualTo(dto.getSectionKey().get());
        assertThat(section.isActive()).isEqualTo(dto.getActive().get());
        assertThat(section.getTitle()).isEqualTo(oldTitle);
        assertThat(section.getContent()).isEqualTo(oldContent);
    }

    @Test
    public void testDelete() throws Exception {

        // given
        var section1Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section1Draft.setPageKey("main");
        var section1 = pageSectionRepository.save(section1Draft);

        var section2Draft = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section2Draft.setPageKey("profile");
        section2Draft.setActive(false);
        var section2 = pageSectionRepository.save(section2Draft);

        var adminToken = givenAdminAccessToken();

        // when
        var result = mockMvc.perform(delete("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"));

        // then
        result.andExpect(status().isSeeOther());
        assertThat(pageSectionRepository.existsById(section1.getId())).isFalse();
        assertThat(pageSectionRepository.existsById(section2.getId())).isTrue();
    }

    @Test
    public void testPostUnauthorizedReturns401() throws Exception {

        // given
        var dto = arbitraryPageSectionCreateDto();

        // when
        var result = mockMvc.perform(post("/api/pages/sections")
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testPostAsNonAdminReturns403() throws Exception {

        // given
        userRepository.save(User.builder()
                .email(CANDIDATE_EMAIL)
                .encryptedPassword(encoder.encode("password"))
                .role(RoleType.CANDIDATE)
                .build());
        var dto = arbitraryPageSectionCreateDto();
        var candidateToken = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);

        // when
        var result = mockMvc.perform(post("/api/pages/sections")
                .cookie(new Cookie("access_token", candidateToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isForbidden());
    }
}
