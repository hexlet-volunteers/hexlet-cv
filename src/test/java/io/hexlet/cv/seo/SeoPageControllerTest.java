package io.hexlet.cv.seo;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.Resume;
import io.hexlet.cv.model.User;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = "app.public-url=https://cv.example.test")
class SeoPageControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    private MockMvc mockMvc;
    private User user;
    private Resume published;
    private Resume draft;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();
        userRepository.findByEmail("seo@example.com").ifPresent(this::remove);
        user = new User();
        user.setEmail("seo@example.com");
        user.setFirstName("Мария");
        user.setLastName("Иванова");
        user.setRole(RoleType.CANDIDATE);
        user = userRepository.save(user);
        published = resume("Frontend <script>alert(1)</script> разработчик", "published");
        draft = resume("Черновик", "draft");
    }

    @AfterEach
    void tearDown() {
        remove(user);
    }

    @Test
    void resumePageHasMetaAndContentForCrawlers() throws Exception {
        mockMvc.perform(get("/resumes/{id}", published.getId()).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Мария Иванова | Хекслет Карьера</title>")))
                .andExpect(content().string(containsString(
                        "<link rel=\"canonical\" href=\"https://cv.example.test/resumes/" + published.getId())))
                .andExpect(content().string(containsString("<meta name=\"description\" content=\"Пишу на React")))
                .andExpect(content().string(containsString("<h2>Навыки</h2><p>React, TypeScript</p>")))
                .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
                .andExpect(content().string(not(containsString("noindex"))));
    }

    @Test
    void draftAndMissingResumeAreRealNotFound() throws Exception {
        mockMvc.perform(get("/resumes/{id}", draft.getId()).accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("noindex")));
        mockMvc.perform(get("/resumes/999999999").accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchResultsAreNotIndexed() throws Exception {
        mockMvc.perform(get("/resumes").param("q", "react").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("noindex")));
        mockMvc.perform(get("/resumes").accept(MediaType.TEXT_HTML))
                .andExpect(content().string(not(containsString("noindex"))))
                .andExpect(content().string(containsString("/resumes/" + published.getId())));
    }

    @Test
    void profileListsPublishedResumes() throws Exception {
        mockMvc.perform(get("/profiles/{id}", user.getId()).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>Мария Иванова</h1>")))
                .andExpect(content().string(containsString("/resumes/" + published.getId())))
                .andExpect(content().string(not(containsString("/resumes/" + draft.getId() + "\""))));
    }

    @Test
    void sitemapListsOnlyPublished() throws Exception {
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "<loc>https://cv.example.test/resumes/" + published.getId() + "</loc>")))
                .andExpect(content().string(containsString(
                        "<loc>https://cv.example.test/profiles/" + user.getId() + "</loc>")))
                .andExpect(content().string(not(containsString("/resumes/" + draft.getId() + "<"))));
    }

    @Test
    void robotsPointsToSitemap() throws Exception {
        mockMvc.perform(get("/robots.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Disallow: /app/")))
                .andExpect(content().string(containsString("Sitemap: https://cv.example.test/sitemap.xml")));
    }

    private Resume resume(String name, String state) {
        var r = new Resume();
        r.setUser(user);
        r.setName(name);
        r.setState(state);
        r.setAnswersCount(0);
        r.setCity("Пермь");
        r.setSummary("Пишу на React и TypeScript, ищу первую работу.");
        r.setSkillsDescription("React, TypeScript");
        return resumeRepository.save(r);
    }

    private void remove(User u) {
        resumeRepository.deleteAll(resumeRepository.findByUserId(u.getId()));
        userRepository.delete(u);
    }
}
