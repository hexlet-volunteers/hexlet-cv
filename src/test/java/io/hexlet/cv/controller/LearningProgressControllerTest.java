    package io.hexlet.cv.controller;

    import static org.assertj.core.api.Assertions.assertThat;
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
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.MvcResult;
    import org.springframework.test.web.servlet.ResultActions;

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

        @AfterEach
        void cleanUp() {
            userLessonProgressRepository.deleteAll();
            userProgramProgressRepository.deleteAll();
            lessonRepository.deleteAll();
            programRepository.deleteAll();
            userRepository.deleteAll();
        }

        private User createUser(String emailPrefix, String role) {
            User user = new User();
            String uniqueEmail = emailPrefix + "_" + System.currentTimeMillis() + "_" + Math.random() + "@example.com";
            user.setEmail(uniqueEmail);
            user.setRole(RoleType.valueOf(role));
            user.setEncryptedPassword(passwordEncoder.encode("password"));
            return userRepository.save(user);
        }

        private String generateToken(User user) {
            return jwtUtils.generateAccessToken(user.getEmail());
        }

        private Program createProgram(String titlePrefix) {
            Program program = new Program();
            String uniqueTitle = titlePrefix + "_" + System.currentTimeMillis() + "_" + Math.random();
            program.setTitle(uniqueTitle);
            program.setDescription(uniqueTitle + "Description");
            return programRepository.save(program);
        }

        private Lesson createLesson(Program program, String titlePrefix, int orderNumber) {
            Lesson lesson = new Lesson();
            String uniqueTitle = titlePrefix + "_" + System.currentTimeMillis() + "_" + Math.random();
            lesson.setTitle(uniqueTitle);
            lesson.setProgram(program);
            lesson.setContent(uniqueTitle + "Content");
            lesson.setOrderNumber(orderNumber);
            return lessonRepository.save(lesson);
        }

        private UserProgramProgress createProgramProgress(User user, Program program) {
            UserProgramProgress progress = new UserProgramProgress();
            progress.setUser(user);
            progress.setProgram(program);
            progress.setStartedAt(LocalDateTime.now());
            progress.setCompletedLessons(0);
            progress.setIsCompleted(false);
            return userProgramProgressRepository.save(progress);
        }

        private UserLessonProgress createLessonProgress(User user, Lesson lesson, UserProgramProgress programProgress) {
            UserLessonProgress progress = new UserLessonProgress();
            progress.setUser(user);
            progress.setLesson(lesson);
            progress.setProgramProgress(programProgress);
            progress.setStartedAt(LocalDateTime.now());
            progress.setIsCompleted(false);
            progress.setTimeSpentMinutes(0);
            return userLessonProgressRepository.save(progress);
        }

        @Test
        void testCandidateAccessMyProgress() throws Exception {
            // given
            User user = createUser("candidate1", "CANDIDATE");
            String token = generateToken(user);

            // when
            ResultActions result =  mockMvc.perform(get("/account/my-progress")
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"));
            // then
            result.andExpect(status().isOk());
        }

        @Test
        void testCandidateAccessMyProgressWithPagination() throws Exception {
            // given
            User user = createUser("candidate2", "CANDIDATE");
            String token = generateToken(user);

            // when
            MvcResult result = mockMvc.perform(get("/account/my-progress")
                            .param("page", "0")
                            .param("size", "5")
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String content = result.getResponse().getContentAsString();
            assertThat(content).contains("progress");
            assertThat(content).contains("pagination");
        }

        @Test
        void testGetLessonProgressWithPagination() throws Exception {
            // given
            User user = createUser("candidate3", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test program");
            UserProgramProgress programProgress = createProgramProgress(user, program);

            for (int i = 0; i < 3; i++) {
                Lesson lesson = createLesson(program, "Lesson " + i, i);
                UserLessonProgress lessonProgress = createLessonProgress(user, lesson, programProgress);

                if (i % 2 == 0) {
                    lessonProgress.setIsCompleted(true);
                    lessonProgress.setCompletedAt(LocalDateTime.now());
                    userLessonProgressRepository.save(lessonProgress);
                }
            }

            // when
            MvcResult result = mockMvc.perform(get("/account/my-progress/program/{programProgressId}/lessons",
                            programProgress.getId())
                            .param("page", "0")
                            .param("size", "2")
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String content = result.getResponse().getContentAsString();
            assertThat(content).contains("lessonsProgress");
            assertThat(content).contains("pagination");
        }

        @Test
        void testGetLessonProgressWithDefaultPagination() throws Exception {
            // given
            User user = createUser("candidate4", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test program");
            UserProgramProgress programProgress = createProgramProgress(user, program);
            Lesson lesson = createLesson(program, "Lesson", 1);

            // when
            ResultActions result = mockMvc.perform(get("/account/my-progress/program/{programProgressId}/lessons",
                            programProgress.getId())
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"));
            // then
            result.andExpect(status().isOk());
        }


        @Test
        void testStartProgram() throws Exception {
            // given
            User user = createUser("candidat5", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test program");

            // when
            mockMvc.perform(post("/account/my-progress/program/start")
                            .param("programId", program.getId().toString())
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isFound());

            // then
            Optional<UserProgramProgress> progress = userProgramProgressRepository
                    .findByUserIdAndProgramId(user.getId(), program.getId());
            assertThat(progress).isPresent();
            assertThat(progress.get().getStartedAt()).isNotNull();
            assertThat(progress.get().getIsCompleted()).isFalse();
        }

        @Test
        void testStartLesson() throws Exception {
            // given
            User user = createUser("candidate6", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test program");
            UserProgramProgress programProgress = createProgramProgress(user, program);
            Lesson lesson = createLesson(program, "Lesson", 1);

            // when
            mockMvc.perform(post("/account/my-progress/lesson/start")
                            .param("programProgressId", programProgress.getId().toString())
                            .param("lessonId", lesson.getId().toString())
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isFound());

            // then
            Optional<UserLessonProgress> lessonProgress = userLessonProgressRepository
                    .findByUserIdAndLessonId(user.getId(), lesson.getId());
            assertThat(lessonProgress).isPresent();
            assertThat(lessonProgress.get().getStartedAt()).isNotNull();
            assertThat(lessonProgress.get().getProgramProgress().getId()).isEqualTo(programProgress.getId());
            assertThat(lessonProgress.get().getIsCompleted()).isFalse();
        }


        @Test
        void testCompleteLesson() throws Exception {
            // given
            User user = createUser("candidate7", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test Program");
            Lesson lesson = createLesson(program, "Test Lesson", 1);
            UserProgramProgress programProgress = createProgramProgress(user, program);
            UserLessonProgress lessonProgress = createLessonProgress(user, lesson, programProgress);

            // when
            mockMvc.perform(post("/account/my-progress/lesson/" + lessonProgress.getId() + "/complete")
                            .param("programProgressId", programProgress.getId().toString())
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isFound());

            // then
            Optional<UserLessonProgress> completedLesson = userLessonProgressRepository
                    .findById(lessonProgress.getId());

            assertThat(completedLesson).isPresent();
            assertThat(completedLesson.get().getCompletedAt()).isNotNull();
            assertThat(completedLesson.get().getIsCompleted()).isTrue();
        }


        @Test
        void testCompleteProgram() throws Exception {
            // given
            User user = createUser("candidate8", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test Program");
            UserProgramProgress programProgress = createProgramProgress(user, program);

            // when
            mockMvc.perform(post("/account/my-progress/program/" + programProgress.getId() + "/complete")
                            .param("programProgressId", programProgress.getId().toString())
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"))
                    .andExpect(status().isFound());

            // then
            Optional<UserProgramProgress> completedProgram = userProgramProgressRepository
                    .findByUserIdAndProgramId(user.getId(), program.getId());
            assertThat(completedProgram).isPresent();
            assertThat(completedProgram.get().getCompletedAt()).isNotNull();
            assertThat(completedProgram.get().getIsCompleted()).isTrue();
        }

        @Test
        void testDefaultSectionRedirect() throws Exception {
            // given
            User user = createUser("candidate9", "CANDIDATE");
            String token = generateToken(user);

            // when
            ResultActions result = mockMvc.perform(get("/account/my-progress/")
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"));

            // then
            result.andExpect(status().is3xxRedirection());
        }

        @Test
        void testCompletedLessonsCount() {
            // given
            User user = createUser("candidate10", "CANDIDATE");
            Program program = createProgram("Test Program");
            UserProgramProgress programProgress = createProgramProgress(user, program);
            for (int i = 0; i < 2; i++) {
                Lesson lesson = createLesson(program, "Lesson " + i, i);
                UserLessonProgress lessonProgress = createLessonProgress(user, lesson, programProgress);
                lessonProgress.setIsCompleted(true);
                lessonProgress.setCompletedAt(LocalDateTime.now());
                userLessonProgressRepository.save(lessonProgress);
            }

            // when
            var completedCount = userLessonProgressRepository
                    .countCompletedLessonsByProgramProgressId(programProgress.getId());

            // then
            assertThat(completedCount).isEqualTo(2L);
        }

        @Test
        void testGetProgressWithoutAuthentication() throws Exception {
            // given - нет токена

            // when
            ResultActions result = mockMvc.perform(get("/account/my-progress")
                            .header("X-Inertia", "true"));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @Test
        void testGetProgressWithAuthentication() throws Exception {
            // given
            User user = createUser("candidate11", "CANDIDATE");
            String token = generateToken(user);

            //when
            ResultActions result = mockMvc.perform(get("/account/my-progress")
                            .cookie(new Cookie("access_token", token))
                            .header("X-Inertia", "true"));
            // then
            result.andExpect(status().isOk());
        }

        @Test
        void completeLessonByAnotherUserReturnsForbidden() throws Exception {
            // given
            User user = createUser("candidate12", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test Program");
            Lesson lesson = createLesson(program, "Test Lesson", 1);
            UserProgramProgress programProgress = createProgramProgress(user, program);
            UserLessonProgress lessonProgress = createLessonProgress(user, lesson, programProgress);

            User anotherUser = createUser("another1", "CANDIDATE");
            String anotherToken = generateToken(anotherUser);

            // when
            ResultActions result = mockMvc.perform(post("/account/my-progress/lesson/" + lessonProgress.getId() + "/complete")
                    .param("programProgressId", programProgress.getId().toString())
                    .cookie(new Cookie("access_token", anotherToken))
                    .header("X-Inertia", true));

            // then
            result.andExpect(status().isForbidden());
        }

        @Test
        void completeProgramByAnotherUserReturnsForbidden() throws Exception {
            // given
            User user = createUser("candidate13", "CANDIDATE");
            String token = generateToken(user);
            Program program = createProgram("Test Program");
            UserProgramProgress programProgress = createProgramProgress(user, program);

            User anotherUser = createUser("another2", "CANDIDATE");
            String anotherToken = generateToken(anotherUser);

            // when
            ResultActions result = mockMvc.perform(post("/account/my-progress/program/" + programProgress.getId() + "/complete")
                            .param("programProgressId", programProgress.getId().toString())
                            .cookie(new Cookie("access_token", anotherToken))
                            .header("X-Inertia", true));
            // then
            result.andExpect(status().isForbidden());
        }
    }
