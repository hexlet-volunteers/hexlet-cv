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
import io.hexlet.cv.mapper.PageSectionMapper;
import io.hexlet.cv.model.PageSection;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.PageSectionRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import io.hexlet.cv.utils.ModelGenerator;
import jakarta.servlet.http.Cookie;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
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
    private ObjectMapper om;

    @Autowired
    private PageSectionMapper pageSectionMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private static final String ADMIN_EMAIL = "page_section_admin@example.com";
    private static final String CANDIDATE_EMAIL = "page_section_candidate@example.com";

    private String adminToken;
    private PageSection section1;
    private PageSection section2;

    @BeforeEach
    public void setUp() {

        userRepository.deleteAll();
        var admin = User.builder()
                .email(ADMIN_EMAIL)
                .encryptedPassword(encoder.encode("password"))
                .role(RoleType.ADMIN)
                .build();
        userRepository.save(admin);
        var candidate = User.builder()
                .email(CANDIDATE_EMAIL)
                .encryptedPassword(encoder.encode("password"))
                .role(RoleType.CANDIDATE)
                .build();
        userRepository.save(candidate);
        adminToken = jwtUtils.generateAccessToken(ADMIN_EMAIL);

        pageSectionRepository.deleteAll();

        section1 = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section2 = Instancio.of(modelGenerator.getPageSectionModel()).create();

        section1.setPageKey("main");
        section2.setPageKey("profile");

        section2.setActive(false);

        pageSectionRepository.save(section1);
        pageSectionRepository.save(section2);
    }

    @Test
    public void testGetAll() throws Exception {

        var response = mockMvc.perform(get("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString())
            .contains(section1.getPageKey())
            .contains(section2.getPageKey())
            .contains(section1.getSectionKey())
            .contains(section2.getSectionKey());
    }

    @Test
    public void testGetAllWithParams() throws Exception {

        var section3 = Instancio.of(modelGenerator.getPageSectionModel()).create();
        section3.setPageKey(section1.getPageKey());
        section3.setActive(section2.isActive());
        pageSectionRepository.save(section3);

        var response = mockMvc.perform(get("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .param("page", section1.getPageKey())
                .param("active", String.valueOf(section2.isActive())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString())
            .doesNotContain(section1.getSectionKey())
            .doesNotContain(section2.getSectionKey())
            .contains(section3.getSectionKey());
    }

    @Test
    public void testGet() throws Exception {

        var response = mockMvc.perform(get("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThatJson(response.getContentAsString()).and(
            v -> v.node("props.pageSections[0].id").isEqualTo(section1.getId()),
            v -> v.node("props.pageSections[0].pageKey").isEqualTo(section1.getPageKey()),
            v -> v.node("props.pageSections[0].sectionKey").isEqualTo(section1.getSectionKey())
        );
    }

    @Test
    public void testCreate() throws Exception {

        pageSectionRepository.delete(section1);

        var dto = new PageSectionCreateDTO();
        dto.setPageKey(section1.getPageKey());
        dto.setSectionKey(section1.getSectionKey());
        dto.setTitle(section1.getTitle());
        dto.setContent(section1.getContent());
        dto.setActive(section1.isActive());

        var response = mockMvc.perform(post("/api/pages/sections")
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isFound());

        var section = assertDoesNotThrow(() ->
            pageSectionRepository.findBySectionKey(dto.getSectionKey()).orElseThrow());

        assertThat(section.getPageKey()).isEqualTo(dto.getPageKey());
        assertThat(section.getTitle()).isEqualTo(dto.getTitle());
        assertThat(section.getContent()).isEqualTo(dto.getContent());
        assertThat(section.isActive()).isEqualTo(dto.getActive());
    }

    @Test
    public void testUpdate() throws Exception {

        var dto = new PageSectionUpdateDTO();
        dto.setPageKey(JsonNullable.of("profile"));
        dto.setSectionKey(JsonNullable.of("tech_stack"));
        dto.setActive(JsonNullable.of(false));

        dto.setTitle(JsonNullable.undefined());
        dto.setContent(JsonNullable.undefined());

        var oldTitle = section1.getTitle();
        var oldContent = section1.getContent();

        var response = mockMvc.perform(put("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isSeeOther());

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

        mockMvc.perform(delete("/api/pages/sections/" + section1.getId())
                .cookie(new Cookie("access_token", adminToken))
                .header("X-Inertia", "true"))
            .andExpect(status().isSeeOther());

        assertThat(pageSectionRepository.existsById(section1.getId())).isFalse();
        assertThat(pageSectionRepository.existsById(section2.getId())).isTrue();
    }

    @Test
    public void testPostUnauthorizedReturns401() throws Exception {

        var dto = new PageSectionCreateDTO();
        dto.setPageKey("x");
        dto.setSectionKey("y");
        dto.setTitle("t");
        dto.setContent("c");
        dto.setActive(true);

        mockMvc.perform(post("/api/pages/sections")
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPostAsNonAdminReturns403() throws Exception {

        var dto = new PageSectionCreateDTO();
        dto.setPageKey("x");
        dto.setSectionKey("y");
        dto.setTitle("t");
        dto.setContent("c");
        dto.setActive(true);

        var candidateToken = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);

        mockMvc.perform(post("/api/pages/sections")
                .cookie(new Cookie("access_token", candidateToken))
                .header("X-Inertia", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }
}
