package io.hexlet.cv.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.admin.marketing.Team;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.model.enums.TeamMemberType;
import io.hexlet.cv.model.enums.TeamPosition;
import io.hexlet.cv.repository.TeamRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "admin@example.com";

    @AfterEach
    public void cleanUp() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        teamRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(String email, RoleType role) {
        var user = User.builder()
                .email(email)
                .encryptedPassword(encoder.encode("password"))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private Team createTeamMember(String firstName, String lastName, boolean isPublished) {
        var team = Team.builder()
                .firstName(firstName)
                .lastName(lastName)
                .position(TeamPosition.PRODUCT)
                .memberType(TeamMemberType.MENTOR)
                .avatarUrl("https://example.com/avatar.jpg")
                .isPublished(isPublished)
                .showOnHomepage(true)
                .displayOrder(1)
                .publishedAt(isPublished ? LocalDateTime.now() : null)
                .build();
        return teamRepository.save(team);
    }

    private String generateToken(User user) {
        return jwtUtils.generateAccessToken(user.getEmail());
    }

    @Test
    public void testGetTeamSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        createTeamMember("John", "Doe", true);

        mockMvc.perform(get("/admin/marketing/team")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("team"))
                .andExpect(jsonPath("$.props.team").isArray())
                .andExpect(jsonPath("$.props.team[0].firstName").value("John"))
                .andExpect(jsonPath("$.props.team[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.props.team[0].position").value("PRODUCT"))
                .andExpect(jsonPath("$.props.team[0].memberType").value("MENTOR"));
    }

    @Test
    public void testGetCreateForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/team/create")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("team"))
                .andExpect(jsonPath("$.props.positions").exists())
                .andExpect(jsonPath("$.props.memberTypes").exists());
    }

    @Test
    public void testGetEditForm() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("John", "Doe", true);

        mockMvc.perform(get("/admin/marketing/team/{id}/edit", testTeam.getId())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("team"))
                .andExpect(jsonPath("$.props.teamMember.id").value(testTeam.getId()))
                .andExpect(jsonPath("$.props.teamMember.firstName").value("John"))
                .andExpect(jsonPath("$.props.teamMember.lastName").value("Doe"))
                .andExpect(jsonPath("$.props.positions").exists())
                .andExpect(jsonPath("$.props.memberTypes").exists());
    }

    @Test
    public void testCreateTeamMember() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String teamJson = """
                {
                    "firstName": "Jane",
                    "lastName": "Smith",
                    "position": "PRODUCT",
                    "memberType": "MENTOR",
                    "avatarUrl": "https://example.com/jane-avatar.jpg",
                    "isPublished": false,
                    "showOnHomepage": true,
                    "displayOrder": 2
                }
                """;

        mockMvc.perform(post("/admin/marketing/team")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamJson))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/team"));

        assertEquals(1, teamRepository.count());
        Team savedTeam = teamRepository.findAll().get(0);
        assertEquals("Jane", savedTeam.getFirstName());
        assertEquals("Smith", savedTeam.getLastName());
        assertEquals(TeamPosition.PRODUCT, savedTeam.getPosition());
        assertEquals(TeamMemberType.MENTOR, savedTeam.getMemberType());
        assertFalse(savedTeam.getIsPublished());
        assertTrue(savedTeam.getShowOnHomepage());
        assertEquals(2, savedTeam.getDisplayOrder());
        assertNull(savedTeam.getPublishedAt());
    }

    @Test
    public void testUpdateTeamMember() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("Original", "Name", true);

        String teamJson = """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "position": "DEVELOPER",
                    "memberType": "ADVISOR",
                    "avatarUrl": "https://example.com/updated-avatar.jpg",
                    "isPublished": true,
                    "showOnHomepage": false,
                    "displayOrder": 5
                }
                """;

        mockMvc.perform(put("/admin/marketing/team/{id}", testTeam.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teamJson))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/team"));

        Team updatedTeam = teamRepository.findById(testTeam.getId()).orElseThrow();
        assertEquals("Updated", updatedTeam.getFirstName());
        assertEquals("Name", updatedTeam.getLastName());
        assertEquals(TeamPosition.DEVELOPER, updatedTeam.getPosition());
        assertEquals(TeamMemberType.ADVISOR, updatedTeam.getMemberType());
        assertEquals("https://example.com/updated-avatar.jpg", updatedTeam.getAvatarUrl());
        assertTrue(updatedTeam.getIsPublished());
        assertFalse(updatedTeam.getShowOnHomepage());
        assertEquals(5, updatedTeam.getDisplayOrder());
        assertNotNull(updatedTeam.getPublishedAt());
    }

    @Test
    public void testDeleteTeamMember() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("Team", "Member", true);
        Long teamId = testTeam.getId();

        mockMvc.perform(delete("/admin/marketing/team/{id}", testTeam.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/team"));

        assertFalse(teamRepository.existsById(teamId));
    }

    @Test
    public void testTogglePublishTeam() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("John", "Doe", false);
        boolean initialPublishStatus = testTeam.getIsPublished();

        mockMvc.perform(post("/admin/marketing/team/{id}/toggle-publish", testTeam.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/team"));

        var toggledTeam = teamRepository.findById(testTeam.getId()).orElseThrow();
        assertEquals(!initialPublishStatus, toggledTeam.getIsPublished());
    }

    @Test
    public void testToggleHomepageTeam() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("Homepage", "Member", true);
        boolean initialHomepageStatus = testTeam.getShowOnHomepage();

        mockMvc.perform(post("/admin/marketing/team/{id}/toggle-homepage", testTeam.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/home-components"));

        var toggledTeam = teamRepository.findById(testTeam.getId()).orElseThrow();
        assertEquals(!initialHomepageStatus, toggledTeam.getShowOnHomepage());
    }

    @Test
    public void testUpdateTeamDisplayOrder() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("Display", "Order", true);

        String json = "{\"display_order\": 3}";

        mockMvc.perform(put("/admin/marketing/team/{id}/display-order", testTeam.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        var updatedTeam = teamRepository.findById(testTeam.getId()).orElseThrow();
        assertEquals(3, updatedTeam.getDisplayOrder());
    }

    @Test
    public void testGetHomeComponentsSection() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);
        var testTeam = createTeamMember("Homepage", "Team", true);
        testTeam.setShowOnHomepage(true);
        teamRepository.save(testTeam);
        mockMvc.perform(get("/admin/marketing/home-components")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("home-components"))
                .andExpect(jsonPath("$.props.team").exists())
                .andExpect(jsonPath("$.props.team").isArray())
                .andExpect(jsonPath("$.props.team[0].firstName").value("Homepage"))
                .andExpect(jsonPath("$.props.team[0].lastName").value("Team"));
    }


    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/admin/marketing/team")
                        .header("X-Inertia", "true"))
                .andExpect(status().isUnauthorized());
    }
}
