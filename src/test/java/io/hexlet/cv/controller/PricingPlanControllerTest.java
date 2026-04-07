package io.hexlet.cv.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.admin.marketing.PricingPlan;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.PricingPlanRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PricingPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String ADMIN_EMAIL = "admin_pricing@example.com";
    private static final String CANDIDATE_EMAIL = "candidate_pricing@example.com";


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        pricingPlanRepository.deleteAll();

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        pricingPlanRepository.deleteAll();
    }

    private User createUser(String email, RoleType role) {
        var user = User.builder()
                .email(email)
                .encryptedPassword(encoder.encode("password"))
                .role(role)
                .build();
        return userRepository.save(user);

    }

    private PricingPlan createPricingPlan(String name, Double originalPrice, Double discountPercent) {
        var plan = PricingPlan.builder()
                .name(name)
                .description("Test Description")
                .originalPrice(originalPrice)
                .discountPercent(discountPercent)
                .build();
        plan.calculateFinalPrice();
        return pricingPlanRepository.save(plan);
    }

    private String generateToken(User user) {
        return jwtUtils.generateAccessToken(user.getEmail());
    }

    @Test
    void testGetPricingSectionAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/pricing")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("pricing"))
                .andExpect(jsonPath("$.props.pricing").exists());
    }

    @Test
    void testGetPricingSectionAsCandidateForbidden() throws Exception {
        var candidate = createUser(CANDIDATE_EMAIL, RoleType.CANDIDATE);
        String candidateToken = generateToken(candidate);

        mockMvc.perform(get("/admin/marketing/pricing")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPricingCreateFormAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/pricing/create")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("pricing"));
    }

    @Test
    void testGetPricingCreateFormAsCandidateForbidden() throws Exception {
        var candidate = createUser(CANDIDATE_EMAIL, RoleType.CANDIDATE);
        String candidateToken = generateToken(candidate);

        mockMvc.perform(get("/admin/marketing/pricing/create")
                        .cookie(new Cookie("access_token", candidateToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPricingEditFormAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        var pricingPlan = createPricingPlan("Test plan", 100.0, 10.0);

        mockMvc.perform(get("/admin/marketing/pricing/" + pricingPlan.getId() + "/edit")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.props.activeMainSection").value("marketing"))
                .andExpect(jsonPath("$.props.activeSubSection").value("pricing"))
                .andExpect(jsonPath("$.props.pricing.id").value(pricingPlan.getId()));
    }

    @Test
    void testGetPricingEditFormAsCandidateForbidden() throws Exception {
        var candidate = createUser(CANDIDATE_EMAIL, RoleType.CANDIDATE);
        String candidateToken = generateToken(candidate);

        var pricingPlan = createPricingPlan("Test Plan", 100.0, 10.0);

        mockMvc.perform(get("/admin/marketing/pricing/" + pricingPlan.getId() + "/edit")
                        .cookie(new Cookie("access_token", candidateToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreatePricingAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String createPricingJson = """
                {
                    "name": "Новый тарифный план",
                    "originalPrice": 150.0,
                    "discountPercent": 20.0,
                    "description": "Описание нового плана"
                }
                """;

        mockMvc.perform(post("/admin/marketing/pricing")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPricingJson)
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/marketing/pricing"));

        assertEquals(1, pricingPlanRepository.count());
        var pricingPlan = pricingPlanRepository.findAll().get(0);
        assertEquals("Новый тарифный план", pricingPlan.getName());
        assertEquals(150.0, pricingPlan.getOriginalPrice());
        assertEquals(20.0, pricingPlan.getDiscountPercent());
        assertEquals(120.0, pricingPlan.getFinalPrice());
    }


    @Test
    void testCreatePricingAsCandidateForbidden() throws Exception {
        var candidate = createUser(CANDIDATE_EMAIL, RoleType.CANDIDATE);
        String candidateToken = generateToken(candidate);

        String createPricingJson = """
                {
                    "name": "Новый план",
                    "originalPrice": 150.0
                }
                """;

        mockMvc.perform(post("/admin/marketing/pricing")
                        .with(csrf())
                        .cookie(new Cookie("access_token", candidateToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPricingJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreatePricingInvalidData() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String invalidPricingJson = """
                {
                    "name": "",
                    "originalPrice": -100.0
                }
                """;

        mockMvc.perform(post("/admin/marketing/pricing")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPricingJson))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdatePricingAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        var pricingPlan = createPricingPlan("Old Plan", 100.0, 10.0);

        String updatePricingJson = """
                {
                    "name": "Обновленное название",
                    "originalPrice": 200.0,
                    "discountPercent": 25.0,
                    "description": "Обновленное описание"
                }
                """;

        mockMvc.perform(put("/admin/marketing/pricing/" + pricingPlan.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePricingJson)
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/pricing"));

        PricingPlan updated = pricingPlanRepository.findById(pricingPlan.getId()).orElseThrow();
        assertEquals("Обновленное название", updated.getName());
        assertEquals(200.0, updated.getOriginalPrice());
        assertEquals(25.0, updated.getDiscountPercent());
        assertEquals(150.0, updated.getFinalPrice());
    }

    @Test
    void testUpdatePricingNotFound() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        String updatePricingJson = """
                {
                    "name": "Обновленное название"
                }
                """;

        mockMvc.perform(put("/admin/marketing/pricing/99999")
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePricingJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePricingAsAdmin() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        var pricingPlan = createPricingPlan("План на удаление", 200.0, 20.0);
        var planId = pricingPlan.getId();

        mockMvc.perform(delete("/admin/marketing/pricing/" + planId)
                        .with(csrf())
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/admin/marketing/pricing"));


        assertFalse(pricingPlanRepository.existsById(planId));
    }

    @Test
    void testDeletePricingAsCandidateForbidden() throws Exception {
        var candidate = createUser(CANDIDATE_EMAIL, RoleType.CANDIDATE);
        String candidateToken = generateToken(candidate);

        var pricingPlan = createPricingPlan("План на удаление", 200.0, 20.0);

        mockMvc.perform(delete("/admin/marketing/pricing/" + pricingPlan.getId())
                        .with(csrf())
                        .cookie(new Cookie("access_token", candidateToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidSectionNotFound() throws Exception {
        var admin = createUser(ADMIN_EMAIL, RoleType.ADMIN);
        String adminToken = generateToken(admin);

        mockMvc.perform(get("/admin/marketing/invalid-section")
                        .cookie(new Cookie("access_token", adminToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isSeeOther());
    }
}
