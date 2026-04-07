package io.hexlet.cv.service;

import io.hexlet.cv.converter.ProgressDtoConverter;
import io.hexlet.cv.dto.learning.UserLessonProgressDTO;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.model.learning.UserLessonProgress;
import io.hexlet.cv.repository.LessonRepository;
import io.hexlet.cv.repository.UserLessonProgressRepository;
import io.hexlet.cv.repository.UserProgramProgressRepository;
import io.hexlet.cv.repository.UserRepository;


import org.springframework.security.access.AccessDeniedException;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class UserLessonProgressService {

    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserProgramProgressRepository userProgramProgressRepository;
    private final ProgressDtoConverter progressDtoConverter;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public Page<UserLessonProgressDTO> getLessonProgress(Long userId, Long programProgressId, Pageable pageable) {
        log.debug("Getting lessons for user{}, programProgress {} (pageable={})", userId, programProgressId, pageable);

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, UserLessonProgress.FIELD_STARTED_AT)
        );

        return userLessonProgressRepository.findByUserIdAndProgramProgressId(userId, programProgressId, sortedPageable)
                .map(progressDtoConverter::convertLessonProgress);
    }

    @Transactional
    public void startLesson(Long userId, Long programProgressId, Long lessonId) {
        if (userLessonProgressRepository.findByUserIdAndLessonId(userId, lessonId).isPresent()) {
            return;
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found" + userId));

        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.not.found" + lessonId));
        var userProgramProgress = userProgramProgressRepository.findById(programProgressId)
                .orElseThrow(() -> new ResourceNotFoundException("program.progress.not.found"
                        + programProgressId));

        LocalDateTime now = LocalDateTime.now(clock);
        var progress = UserLessonProgress.builder()
                .user(user)
                .lesson(lesson)
                .programProgress(userProgramProgress)
                .startedAt(now)
                .isCompleted(false)
                .timeSpentMinutes(0)
                .build();


        userLessonProgressRepository.save(progress);
    }

    @Transactional
    public void completeLesson(Long progressId, Long userId) {
        var progress = userLessonProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("lesson.progress.not.found" + progressId));

        if (!progress.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        progress.setIsCompleted(true);
        progress.setCompletedAt(now);

        userLessonProgressRepository.save(progress);

        updateProgramProgress(progress.getProgramProgress().getId());
    }

    private void updateProgramProgress(Long programProgressId) {
        var programProgress = userProgramProgressRepository.findById(programProgressId)
                .orElseThrow(() -> new ResourceNotFoundException("program.progress.not.found"
                        + programProgressId));

        Long completedLessonsCount =
                userLessonProgressRepository.countCompletedLessonsByProgramProgressId(programProgressId);

        LocalDateTime now = LocalDateTime.now(clock);
        programProgress.setCompletedLessons(completedLessonsCount.intValue());
        programProgress.setLastActivityAt(now);

        int totalLessons = programProgress.getProgram().getLessons().size();
        if (completedLessonsCount == totalLessons) {
            programProgress.setIsCompleted(true);
            programProgress.setCompletedAt(LocalDateTime.now());
        }

        userProgramProgressRepository.save(programProgress);
    }
}
