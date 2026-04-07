package io.hexlet.cv.service;

import io.hexlet.cv.converter.ProgressDtoConverter;
import io.hexlet.cv.dto.learning.UserProgramProgressDTO;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.model.learning.UserProgramProgress;
import io.hexlet.cv.repository.ProgramRepository;
import io.hexlet.cv.repository.UserProgramProgressRepository;
import io.hexlet.cv.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserProgramProgressService {

    private final UserProgramProgressRepository programProgressRepository;
    private final ProgressDtoConverter progressDtoConverter;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final Clock clock;

    public Page<UserProgramProgressDTO> getUserProgress(Long userId, Pageable pageable) {
        log.debug("Getting progress for user {} (pageable={})", userId, pageable);

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, UserProgramProgress.FIELD_STARTED_AT)
        );

        return programProgressRepository.findByUserId(userId, sortedPageable)
                .map(progressDtoConverter::convertProgramProgress);
    }

    @Transactional
    public void startProgram(Long userId, Long programId) {
        log.debug("Starting program {} for user {}", programId, userId);

        if (programProgressRepository.existsByUserIdAndProgramId(userId, programId)) {
            return;
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        var program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("program.not.found"));

        LocalDateTime now = LocalDateTime.now(clock);
        var progress = UserProgramProgress.builder()
                .user(user)
                .program(program)
                .startedAt(now)
                .lastActivityAt(now)
                .completedLessons(0)
                .isCompleted(false)
                .build();

        programProgressRepository.save(progress);
    }

    @Transactional
    public void completeProgram(Long progressId, Long userId) {
        log.debug("Completing program progress {}", progressId);
        var progress = programProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("program.progress.not.found"));

        if (!progress.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        int totalLessons = progress.getProgram().getLessons().size();

        progress.setCompletedLessons(totalLessons);
        progress.setIsCompleted(true);
        progress.setCompletedAt(now);
        progress.setLastActivityAt(now);

        programProgressRepository.save(progress);
    }
}
