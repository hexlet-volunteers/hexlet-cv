package io.hexlet.cv.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hexlet.cv.model.User;
import io.hexlet.cv.model.admin.programs.Lesson;
import io.hexlet.cv.model.admin.programs.Program;
import io.hexlet.cv.model.enums.RoleType;
import io.hexlet.cv.model.learning.UserLessonProgress;
import io.hexlet.cv.model.learning.UserProgramProgress;
import io.hexlet.cv.repository.LessonRepository;
import io.hexlet.cv.repository.ProgramRepository;
import io.hexlet.cv.repository.UserLessonProgressRepository;
import io.hexlet.cv.repository.UserProgramProgressRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class LearningProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserProgramProgressRepository userProgramProgressRepository;

    @Autowired
    private UserLessonProgressRepository userLessonProgressRepository;


    private static final String CANDIDATE_EMAIL = "candidate_user@example.com";
    private String candidateToken;
    private User testUser;
    private Program testProgram;
    private Lesson testLesson;
    private UserProgramProgress testProgramProgress;


    @AfterEach
    void cleanUp() {
        userLessonProgressRepository.deleteAll();
        userProgramProgressRepository.deleteAll();
        lessonRepository.deleteAll();
        programRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        cleanUp();

        testUser = new User();
        testUser.setEmail(CANDIDATE_EMAIL);
        testUser.setEncryptedPassword(passwordEncoder.encode("candidate_password"));
        testUser.setRole(RoleType.CANDIDATE);
        testUser = userRepository.save(testUser);

        candidateToken = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);

        testProgram = new Program();
        testProgram.setTitle("Test Program");
        testProgram.setDescription("Test Program Description");
        testProgram = programRepository.save(testProgram);

        testLesson = new Lesson();
        testLesson.setTitle("Test Lesson");
        testLesson.setContent("Test Lesson Content");
        testLesson.setProgram(testProgram);
        testLesson.setOrderNumber(1);
        testLesson = lessonRepository.save(testLesson);

        testProgramProgress =  new UserProgramProgress();
        testProgramProgress.setUser(testUser);
        testProgramProgress.setProgram(testProgram);
        testProgramProgress.setStartedAt(LocalDateTime.now());
        testProgramProgress = userProgramProgressRepository.save(testProgramProgress);
    }

    @Test
    void testCandidateAccessMyProgress() throws Exception {
        mockMvc.perform(get("/account/my-progress")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testCandidateAccessMyProgressWithPagination() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/my-progress")
                        .param("page", "0")
                        .param("size", "5")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("progress");
        assertThat(content).contains("pagination");
    }

    @Test
    void testGetLessonProgressWithPagination() throws Exception {
        for (int i = 0; i < 3; i++) {
            Lesson lesson = new Lesson();
            lesson.setTitle("Test Lesson " + i);
            lesson.setContent("Test Lesson Content " + i);
            lesson.setProgram(testProgram);
            lesson.setOrderNumber(i + 2);
            lesson = lessonRepository.save(lesson);

            UserLessonProgress lessonProgress = new UserLessonProgress();
            lessonProgress.setUser(testUser);
            lessonProgress.setLesson(lesson);
            lessonProgress.setProgramProgress(testProgramProgress);
            lessonProgress.setStartedAt(LocalDateTime.now());
            lessonProgress.setIsCompleted(i % 2 == 0);
            userLessonProgressRepository.save(lessonProgress);
        }

        MvcResult result = mockMvc.perform(get("/account/my-progress/program/{programProgressId}/lessons",
                        testProgramProgress.getId())
                        .param("page", "0")
                        .param("size", "2")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("lessonsProgress");
        assertThat(content).contains("pagination");
    }

    @Test
    void testGetLessonProgressWithDefaultPagination() throws Exception {
        mockMvc.perform(get("/account/my-progress/program/{programProgressId}/lessons",
                        testProgramProgress.getId())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk());
    }


    @Test
    void testStartProgram() throws Exception {
        mockMvc.perform(post("/account/my-progress/program/start")
                        .with(csrf())
                        .param("programId", testProgram.getId().toString())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound());

        Optional<UserProgramProgress> progress = userProgramProgressRepository
                .findByUserIdAndProgramId(testUser.getId(), testProgram.getId());
        assertThat(progress).isPresent();
        assertThat(progress.get().getStartedAt()).isNotNull();
    }

    @Test
    void testStartLesson() throws Exception {
        mockMvc.perform(post("/account/my-progress/lesson/start")
                        .with(csrf())
                        .param("programProgressId", testProgramProgress.getId().toString())
                        .param("lessonId", testLesson.getId().toString())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound());

        Optional<UserLessonProgress> lessonProgress = userLessonProgressRepository
                .findByUserIdAndLessonId(testUser.getId(), testLesson.getId());
        assertThat(lessonProgress).isPresent();
        assertThat(lessonProgress.get().getStartedAt()).isNotNull();
        assertThat(lessonProgress.get().getProgramProgress().getId()).isEqualTo(testProgramProgress.getId());
    }


    @Test
    void testCompleteLesson() throws Exception {
        UserLessonProgress lessonProgress = new UserLessonProgress();
        lessonProgress.setUser(testUser);
        lessonProgress.setLesson(testLesson);
        lessonProgress.setProgramProgress(testProgramProgress);
        lessonProgress.setStartedAt(LocalDateTime.now());
        lessonProgress.setIsCompleted(false);
        lessonProgress = userLessonProgressRepository.save(lessonProgress);

        mockMvc.perform(post("/account/my-progress/lesson/" + lessonProgress.getId() + "/complete")
                        .with(csrf())
                        .param("programProgressId", testProgramProgress.getId().toString())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound());

        Optional<UserLessonProgress> completedLesson = userLessonProgressRepository
                .findById(lessonProgress.getId());

        assertThat(completedLesson).isPresent();
        assertThat(completedLesson.get().getCompletedAt()).isNotNull();
        assertThat(completedLesson.get().getIsCompleted()).isTrue();
    }


    @Test
    void testCompleteProgram() throws Exception {
        mockMvc.perform(post("/account/my-progress/program/" + testProgramProgress.getId() + "/complete")
                        .with(csrf())
                        .param("programProgressId", testProgramProgress.getId().toString())
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isFound());

        Optional<UserProgramProgress> completedProgram = userProgramProgressRepository
                .findByUserIdAndProgramId(testUser.getId(), testProgram.getId());
        assertThat(completedProgram).isPresent();
        assertThat(completedProgram.get().getCompletedAt()).isNotNull();
        assertThat(completedProgram.get().getIsCompleted()).isTrue();
    }

    @Test
    void testDefaultSectionRedirect() throws Exception {
        var token = jwtUtils.generateAccessToken(CANDIDATE_EMAIL);

        mockMvc.perform(get("/account/my-progress/")
                        .cookie(new Cookie("access_token", token))
                        .header("X-Inertia", "true"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testCompletedLessonsCount() {
        UserLessonProgress userLessonProgress = new UserLessonProgress();
        userLessonProgress.setUser(testUser);
        userLessonProgress.setLesson(testLesson);
        userLessonProgress.setProgramProgress(testProgramProgress);
        userLessonProgress.setStartedAt(LocalDateTime.now());
        userLessonProgress.setCompletedAt(LocalDateTime.now());
        userLessonProgress.setIsCompleted(true);
        userLessonProgressRepository.save(userLessonProgress);

        var testLesson2 = new Lesson();
        testLesson2.setTitle("Test Lesson 2");
        testLesson2.setContent("Test Lesson Content 2");
        testLesson2.setProgram(testProgram);
        testLesson2.setOrderNumber(2);
        testLesson2 = lessonRepository.save(testLesson2);

        var userLessonProgress1 = new UserLessonProgress();
        userLessonProgress1.setUser(testUser);
        userLessonProgress1.setLesson(testLesson2);
        userLessonProgress1.setProgramProgress(testProgramProgress);
        userLessonProgress1.setStartedAt(LocalDateTime.now());
        userLessonProgress1.setCompletedAt(LocalDateTime.now());
        userLessonProgress1.setIsCompleted(true);
        userLessonProgressRepository.save(userLessonProgress1);

        var completedCount = userLessonProgressRepository
                .countCompletedLessonsByProgramProgressId(testProgramProgress.getId());
        assertThat(completedCount).isEqualTo(2L);
    }

    @Test
    void testGetProgressWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/account/my-progress")
                        .header("X-Inertia", "true"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetProgressWithAuthentication() throws Exception {
        mockMvc.perform(get("/account/my-progress")
                        .cookie(new Cookie("access_token", candidateToken))
                        .header("X-Inertia", "true"))
                .andExpect(status().isOk());
    }
}
