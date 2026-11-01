package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.model.CareerMember;
import io.hexlet.cv.model.CareerStepMember;
import io.hexlet.cv.model.ResumeAnswer;
import io.hexlet.cv.model.ResumeAnswerComment;
import io.hexlet.cv.model.ResumeComment;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.CareerItemRepository;
import io.hexlet.cv.repository.CareerMemberRepository;
import io.hexlet.cv.repository.CareerStepMemberRepository;
import io.hexlet.cv.repository.ResumeAnswerCommentRepository;
import io.hexlet.cv.repository.ResumeAnswerLikeRepository;
import io.hexlet.cv.repository.ResumeAnswerRepository;
import io.hexlet.cv.repository.ResumeCommentRepository;
import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPageService {

    private UserRepository userRepository;
    private ResumeRepository resumeRepository;
    private ResumeAnswerRepository resumeAnswerRepository;
    private ResumeAnswerCommentRepository resumeAnswerCommentRepository;
    private ResumeAnswerLikeRepository resumeAnswerLikeRepository;
    private ResumeCommentRepository resumeCommentRepository;
    private CareerMemberRepository careerMemberRepository;
    private CareerStepMemberRepository careerStepMemberRepository;
    private CareerItemRepository careerItemRepository;

    public Map<String, Object> buildProps(Long userId) {
        Map<String, Object> props = new HashMap<>();

        User mainUserData = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден")
        );


        props.putAll(buildUserInfo(mainUserData));
        props.putAll(buildUserStats(userId));
        props.put("resumes", buildResumes(userId));
        props.put("recentAnswers", buildRecentAnswers(userId));
        props.put("userRecommendation", buildRecommendations(userId));
        props.put("resumeComments", buildResumeComments(userId));
        props.put("careerTracks", buildCareerTracks(userId));

        return props;
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("firstName", user.getFirstName());
        info.put("lastName", user.getLastName());
        info.put("role", user.getRole() != null ? user.getRole().name().toLowerCase() : null);
        return info;
    }

    private Map<String, Object> buildUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnswers", resumeAnswerRepository.countByUserId(userId));
        stats.put("totalComments", resumeAnswerCommentRepository.countByUserId(userId));
        stats.put("totalLikes", resumeAnswerLikeRepository.countByAnswerUserId(userId));
        return stats;
    }

    private List<Map<String, Object>> buildResumes(Long userId) {
        return Optional.ofNullable(resumeRepository.findByUserId(userId))
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(resume -> {
                    Map<String, Object> resumeMap = new HashMap<>();
                    resumeMap.put("name", resume.getName());
                    resumeMap.put("id", resume.getId());
                    resumeMap.put("summary", resume.getSummary());
                    resumeMap.put("answerCount", resume.getAnswersCount());
                    resumeMap.put("impressionsCount", resume.getImpressionsCount());
                    resumeMap.put("createdAt", resume.getCreatedAt().toString());
                    return resumeMap;
                }).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    private List<Map<String, Object>> buildRecentAnswers(Long userId) {
        Pageable pageable = PageRequest.of(0, 10);
        List<ResumeAnswer> answers = resumeAnswerRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return Optional.ofNullable(answers)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(answer -> {
                    Map<String, Object> answerMap = new HashMap<>();
                    answerMap.put("resumeTitle", answer.getId());
                    answerMap.put("content", answer.getContent());
                    answerMap.put("answerAuthor", answer.getResume().getUser().getFirstName()
                            + " " + answer.getResume().getUser().getLastName());
                    answerMap.put("createdAt", answer.getCreatedAt().toString());
                    answerMap.put("resumeId", answer.getResume().getId());
                    answerMap.put("userAnswerId", answer.getUser().getId());
                    return answerMap;
                }).toList())
                .orElseGet(Collections::emptyList);
    }

    private List<Map<String, Object>> buildRecommendations(Long userId) {
        List<ResumeAnswerComment> comments = resumeAnswerCommentRepository.findByUserId(userId);

        return Optional.ofNullable(comments)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(rec -> {
                    Map<String, Object> recMap = new HashMap<>();
                    recMap.put("authorName", rec.getResume().getUser().getFirstName()
                            + " " + rec.getResume().getUser().getLastName());
                    recMap.put("AuthorId", rec.getUser().getId());
                    recMap.put("content", rec.getContent());
                    recMap.put("recommendationId", rec.getId());
                    return recMap;
                }).toList())
                .orElseGet(Collections::emptyList);
    }

    private List<Map<String, Object>> buildResumeComments(Long userId) {
        List<ResumeComment> comments = resumeCommentRepository.findByResumeUserId(userId);

        return Optional.ofNullable(comments)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(comment -> {
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("authorName",
                            comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                    commentMap.put("authorId", comment.getUser().getId());
                    commentMap.put("content", comment.getContent());
                    commentMap.put("resumeId", comment.getResume().getId());
                    return commentMap;
                }).toList())
                .orElseGet(Collections::emptyList);
    }

    private List<Map<String, Object>> buildCareerTracks(Long userId) {
        List<CareerMember> members = careerMemberRepository.findByUserId(userId);

        return Optional.ofNullable(members)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(member -> {
                    Map<String, Object> careerMap = new HashMap<>();
                    careerMap.put("careerName", member.getCareer().getName());
                    careerMap.put("status", member.getState().toString());
                    careerMap.put("finishedAt", member.getFinishedAt());

                    int completedStepsCount = careerStepMemberRepository
                            .countByCareerMemberIdAndState(member.getId(), "COMPLETED");
                    int totalSteps = careerItemRepository.countByCareerId(member.getCareer().getId());

                    careerMap.put("completedStepsCount", completedStepsCount);
                    careerMap.put("totalSteps", totalSteps);
                    careerMap.put("careerId", member.getCareer().getId());
                    careerMap.put("careerMemberId", member.getId());

                    List<CareerStepMember> completedSteps =
                            careerStepMemberRepository.findByCareerMemberIdAndState(member.getId(), "COMPLETED");

                    List<Map<String, Object>> completedStepsData = Optional.ofNullable(completedSteps)
                            .filter(steps -> !steps.isEmpty())
                            .map(steps -> steps.stream().map(stepMember -> {
                                Map<String, Object> stepMap = new HashMap<>();
                                stepMap.put("stepName", stepMember.getCareerStep().getName());
                                return stepMap;
                            }).toList())
                            .orElseGet(Collections::emptyList);

                    careerMap.put("completedSteps", completedStepsData);
                    return careerMap;
                }).toList())
                .orElseGet(Collections::emptyList);
    }
}
