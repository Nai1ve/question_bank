package com.onepass.practice.practice;

import com.onepass.practice.common.AppException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PracticeService {

    private final PracticeProperties practiceProperties;
    private final PracticeSessionStore practiceSessionStore;
    private final PracticeQuestionCatalog practiceQuestionCatalog;

    public PracticeService(
            PracticeProperties practiceProperties,
            PracticeSessionStore practiceSessionStore,
            PracticeQuestionCatalog practiceQuestionCatalog
    ) {
        this.practiceProperties = practiceProperties;
        this.practiceSessionStore = practiceSessionStore;
        this.practiceQuestionCatalog = practiceQuestionCatalog;
    }

    public PracticeStartResponse startPracticeSession(Long studentId, PracticeStartRequest request) {
        String normalizedCategoryId = normalizeCategoryId(request.categoryId());
        List<String> selectedTags = normalizedTags(request.selectedTags());
        PracticeSessionAggregate activeSession = findAndExpireIfNeeded(studentId, request.entryType(), normalizedCategoryId);

        if (activeSession != null && activeSession.getStatus() == PracticeSessionStatus.ONGOING) {
            return new PracticeStartResponse(
                    false,
                    activeSession.getSessionId(),
                    activeSession.getTotalCount(),
                    activeSession.getEntryType(),
                    activeSession.getCategoryName(),
                    activeSession.getFeedbackMode(),
                    "存在未完成会话，请先继续或重新开始"
            );
        }

        List<PracticeQuestionDefinition> questionPool = "topxx".equals(request.entryType())
                ? buildTopxxQuestionPool(studentId, normalizedCategoryId, selectedTags)
                : buildNormalQuestionPool(normalizedCategoryId, selectedTags);

        if ("normal".equals(request.entryType())) {
            questionPool = filterAnsweredQuestions(studentId, questionPool);
            questionPool = shuffleQuestions(questionPool);
        }

        int questionCount = request.questionCount() == null ? 20 : request.questionCount();
        questionPool = questionPool.stream().limit(questionCount).toList();

        if (questionPool.isEmpty()) {
            return new PracticeStartResponse(
                    false,
                    null,
                    0,
                    request.entryType(),
                    request.categoryName(),
                    request.feedbackMode(),
                    "当前筛选范围暂无可练习题目"
            );
        }

        PracticeSessionAggregate session = new PracticeSessionAggregate();
        session.setSessionId("practice-" + UUID.randomUUID().toString().replace("-", ""));
        session.setStudentId(studentId);
        session.setEntryType(request.entryType());
        session.setCategoryId(normalizedCategoryId);
        session.setCategoryName(request.categoryName());
        session.setFeedbackMode(request.feedbackMode());
        session.setStatus(PracticeSessionStatus.ONGOING);
        session.setCurrentIndex(0);
        session.setStartedAt(Instant.now());
        session.setLastActiveAt(session.getStartedAt());
        session.setVersion(0L);
        session.setQuestions(buildQuestionSnapshots(questionPool));

        practiceSessionStore.createSession(session);

        return new PracticeStartResponse(
                true,
                session.getSessionId(),
                session.getTotalCount(),
                session.getEntryType(),
                session.getCategoryName(),
                session.getFeedbackMode(),
                null
        );
    }

    public PracticeSessionView findActiveSession(Long studentId, PracticeActiveSessionQuery query) {
        PracticeSessionAggregate session = findAndExpireIfNeeded(studentId, query.entryType(), normalizeCategoryId(query.categoryId()));
        return session == null || session.getStatus() != PracticeSessionStatus.ONGOING ? null : toSessionView(session);
    }

    public PracticeSessionView getPracticeSession(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireSession(studentId, sessionId);
        if (expireIfNeeded(session)) {
            return toSessionView(session);
        }

        touchSession(session);
        practiceSessionStore.saveSessionProgress(session);
        return toSessionView(session);
    }

    public PracticeAnswerSubmitResponse submitPracticeAnswer(Long studentId, String sessionId, PracticeAnswerSubmitRequest request) {
        PracticeSessionAggregate session = requireOngoingSession(studentId, sessionId);
        PracticeQuestionSnapshot question = session.findQuestionById(request.questionId());
        if (question == null) {
            throw new AppException("Question does not exist in current session");
        }

        touchSession(session);

        List<String> normalizedUserAnswer = normalizeAnswer(request.selectedOptions());
        List<String> normalizedStandardAnswer = normalizeAnswer(question.getStandardAnswer());
        boolean correct = isAnswerCorrect(normalizedUserAnswer, normalizedStandardAnswer);

        question.setUserAnswer(normalizedUserAnswer);
        question.setSubmitted(true);
        question.setCorrect(correct);
        question.setAnswerLabel(joinAnswer(normalizedStandardAnswer));
        question.setUserAnswerLabel(normalizedUserAnswer.isEmpty() ? "未作答" : joinAnswer(normalizedUserAnswer));

        practiceSessionStore.saveQuestionAnswer(session, question);
        practiceSessionStore.saveSessionProgress(session);

        return new PracticeAnswerSubmitResponse(
                correct,
                question.getAnswerLabel(),
                question.getUserAnswerLabel(),
                question.getAnalysis()
        );
    }

    public PracticeSessionView moveToNextQuestion(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireOngoingSession(studentId, sessionId);
        touchSession(session);
        session.setCurrentIndex(Math.min(session.getCurrentIndex() + 1, session.getTotalCount() - 1));
        practiceSessionStore.saveSessionProgress(session);
        return toSessionView(session);
    }

    public PracticeSummaryView completePracticeSession(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireSession(studentId, sessionId);
        if (expireIfNeeded(session)) {
            throw new AppException("Practice session is not resumable");
        }

        if (session.getStatus() == PracticeSessionStatus.COMPLETED) {
            return toSummaryView(session);
        }
        if (session.getStatus() != PracticeSessionStatus.ONGOING) {
            throw new AppException("Practice session is not resumable");
        }

        touchSession(session);
        session.setStatus(PracticeSessionStatus.COMPLETED);
        session.setCompletedAt(Instant.now());
        practiceSessionStore.completeSession(session);
        return toSummaryView(session);
    }

    public PracticeSummaryView getPracticeSummary(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireSession(studentId, sessionId);
        if (expireIfNeeded(session)) {
            throw new AppException("Practice session is not resumable");
        }

        if (session.getStatus() == PracticeSessionStatus.COMPLETED) {
            return toSummaryView(session);
        }

        return completePracticeSession(studentId, sessionId);
    }

    public void abandonPracticeSession(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireOngoingSession(studentId, sessionId);
        session.setStatus(PracticeSessionStatus.ABANDONED);
        session.setAbandonedAt(Instant.now());
        session.setLastActiveAt(session.getAbandonedAt());
        practiceSessionStore.markSessionAbandoned(session);
    }

    private PracticeSessionAggregate findAndExpireIfNeeded(Long studentId, String entryType, String categoryId) {
        PracticeSessionAggregate session = practiceSessionStore.findActiveSession(studentId, entryType, categoryId);
        if (session == null) {
            return null;
        }
        expireIfNeeded(session);
        return session;
    }

    private PracticeSessionAggregate requireSession(Long studentId, String sessionId) {
        PracticeSessionAggregate session = practiceSessionStore.findSession(studentId, sessionId);
        if (session == null) {
            throw new AppException("Practice session does not exist");
        }
        return session;
    }

    private PracticeSessionAggregate requireOngoingSession(Long studentId, String sessionId) {
        PracticeSessionAggregate session = requireSession(studentId, sessionId);
        if (expireIfNeeded(session)) {
            throw new AppException("Practice session is not resumable");
        }
        if (session.getStatus() != PracticeSessionStatus.ONGOING) {
            throw new AppException("Practice session is not resumable");
        }
        return session;
    }

    private boolean expireIfNeeded(PracticeSessionAggregate session) {
        if (session.getStatus() != PracticeSessionStatus.ONGOING) {
            return false;
        }

        Instant deadline = session.getLastActiveAt().plus(Duration.ofDays(practiceProperties.getSessionRetentionDays()));
        if (Instant.now().isAfter(deadline)) {
            session.setStatus(PracticeSessionStatus.EXPIRED);
            session.setExpiredAt(Instant.now());
            practiceSessionStore.markSessionExpired(session);
            return true;
        }
        return false;
    }

    private void touchSession(PracticeSessionAggregate session) {
        if (session.getStatus() == PracticeSessionStatus.ONGOING) {
            session.setLastActiveAt(Instant.now());
        }
    }

    private List<PracticeQuestionDefinition> buildNormalQuestionPool(String categoryId, List<String> selectedTags) {
        if (!StringUtils.hasText(categoryId)) {
            throw new AppException("Normal practice requires a leaf category");
        }

        return practiceQuestionCatalog.listAll().stream()
                .filter(question -> categoryId.equals(question.categoryId()))
                .filter(question -> matchTags(question.tags(), selectedTags))
                .toList();
    }

    private List<PracticeQuestionDefinition> buildTopxxQuestionPool(Long studentId, String categoryId, List<String> selectedTags) {
        Map<String, QuestionWrongStatSnapshot> stats = practiceSessionStore.getQuestionStats(
                studentId,
                practiceQuestionCatalog.listAll().stream().map(PracticeQuestionDefinition::id).toList()
        );

        return practiceQuestionCatalog.listAll().stream()
                .filter(question -> !StringUtils.hasText(categoryId) || question.categoryPathIds().contains(categoryId))
                .filter(question -> matchTags(question.tags(), selectedTags))
                .map(question -> new RankedMockQuestion(
                        question,
                        stats.getOrDefault(question.id(), new QuestionWrongStatSnapshot(question.id(), 0, 0)).wrongCount()
                ))
                .filter(question -> question.wrongCount() > 0)
                .sorted(Comparator.comparingInt(RankedMockQuestion::wrongCount).reversed()
                        .thenComparing(question -> question.question().id()))
                .map(RankedMockQuestion::question)
                .toList();
    }

    private List<PracticeQuestionDefinition> filterAnsweredQuestions(Long studentId, List<PracticeQuestionDefinition> questions) {
        Set<String> answeredQuestionIds = practiceSessionStore.listAnsweredQuestionIds(
                studentId,
                questions.stream().map(PracticeQuestionDefinition::id).toList()
        );

        return questions.stream()
                .filter(question -> !answeredQuestionIds.contains(question.id()))
                .toList();
    }

    private List<PracticeQuestionDefinition> shuffleQuestions(List<PracticeQuestionDefinition> questions) {
        List<PracticeQuestionDefinition> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    private List<String> normalizedTags(List<String> selectedTags) {
        if (selectedTags == null || selectedTags.isEmpty()) {
            return List.of();
        }

        List<String> normalized = new ArrayList<>(selectedTags);
        normalized.sort(String::compareTo);
        return normalized;
    }

    private boolean matchTags(List<String> questionTags, List<String> selectedTags) {
        if (selectedTags == null || selectedTags.isEmpty()) {
            return true;
        }

        return selectedTags.stream().anyMatch(questionTags::contains);
    }

    private List<PracticeQuestionSnapshot> buildQuestionSnapshots(List<PracticeQuestionDefinition> questionPool) {
        List<PracticeQuestionSnapshot> result = new ArrayList<>();
        for (int index = 0; index < questionPool.size(); index += 1) {
            PracticeQuestionDefinition source = questionPool.get(index);
            PracticeQuestionSnapshot target = new PracticeQuestionSnapshot();
            target.setQuestionId(source.id());
            target.setOrder(index + 1);
            target.setType(source.type());
            target.setTags(List.copyOf(source.tags()));
            target.setStem(source.stem());
            target.setOptions(source.options().stream()
                    .map(option -> new PracticeQuestionOptionView(option.key(), option.content()))
                    .toList());
            target.setStandardAnswer(List.copyOf(source.answer()));
            target.setAnalysis(source.analysis());
            target.setUserAnswer(new ArrayList<>());
            target.setVersion(0L);
            result.add(target);
        }
        return result;
    }

    private PracticeSessionView toSessionView(PracticeSessionAggregate session) {
        PracticeQuestionSnapshot currentQuestion = session.getStatus() == PracticeSessionStatus.ONGOING
                ? session.getCurrentQuestion()
                : null;

        return new PracticeSessionView(
                session.getSessionId(),
                session.getEntryType(),
                session.getCategoryId(),
                session.getCategoryName(),
                session.getFeedbackMode(),
                session.getStatus().name(),
                session.getCurrentIndex(),
                session.getTotalCount() == 0 ? 0 : session.getCurrentIndex() + 1,
                session.getTotalCount(),
                session.getStatus() == PracticeSessionStatus.COMPLETED,
                session.getStartedAt() == null ? null : session.getStartedAt().toString(),
                session.getLastActiveAt() == null ? null : session.getLastActiveAt().toString(),
                session.getCompletedAt() == null ? null : session.getCompletedAt().toString(),
                session.getExpiredAt() == null ? null : session.getExpiredAt().toString(),
                toQuestionView(currentQuestion)
        );
    }

    private PracticeQuestionView toQuestionView(PracticeQuestionSnapshot question) {
        if (question == null) {
            return null;
        }

        return new PracticeQuestionView(
                question.getQuestionId(),
                question.getType(),
                List.copyOf(question.getTags()),
                question.getStem(),
                question.getOptions().stream()
                        .map(option -> new PracticeQuestionOptionView(option.key(), option.content()))
                        .toList(),
                List.copyOf(question.getUserAnswer())
        );
    }

    private PracticeSummaryView toSummaryView(PracticeSessionAggregate session) {
        int correctCount = 0;
        int wrongCount = 0;

        for (PracticeQuestionSnapshot question : session.getQuestions()) {
            if (question.isCorrect()) {
                correctCount += 1;
            } else {
                wrongCount += 1;
            }
        }

        int totalCount = session.getTotalCount();
        String accuracy = totalCount == 0 ? "0%" : Math.round((correctCount * 100.0) / totalCount) + "%";

        List<PracticeQuestionResultView> results = session.getQuestions().stream()
                .map(question -> new PracticeQuestionResultView(
                        question.getQuestionId(),
                        question.getOrder(),
                        question.getType(),
                        question.getStem(),
                        List.copyOf(question.getTags()),
                        question.getOptions().stream()
                                .map(option -> new PracticeQuestionOptionView(option.key(), option.content()))
                                .toList(),
                        List.copyOf(question.getUserAnswer()),
                        StringUtils.hasText(question.getUserAnswerLabel()) ? question.getUserAnswerLabel() : "未作答",
                        List.copyOf(question.getStandardAnswer()),
                        joinAnswer(question.getStandardAnswer()),
                        question.isCorrect(),
                        question.getAnalysis()
                ))
                .collect(Collectors.toList());

        return new PracticeSummaryView(
                session.getSessionId(),
                session.getEntryType(),
                session.getCategoryName(),
                session.getFeedbackMode(),
                totalCount,
                correctCount,
                wrongCount,
                accuracy,
                results
        );
    }

    private List<String> normalizeAnswer(List<String> answerValues) {
        List<String> normalized = new ArrayList<>(answerValues == null ? List.of() : answerValues);
        normalized.sort(String::compareTo);
        return normalized;
    }

    private boolean isAnswerCorrect(List<String> userAnswer, List<String> standardAnswer) {
        if (userAnswer.size() != standardAnswer.size()) {
            return false;
        }

        for (int index = 0; index < userAnswer.size(); index += 1) {
            if (!userAnswer.get(index).equals(standardAnswer.get(index))) {
                return false;
            }
        }
        return true;
    }

    private String joinAnswer(List<String> answers) {
        return normalizeAnswer(answers).stream().collect(Collectors.joining(", "));
    }

    private String normalizeCategoryId(String categoryId) {
        return StringUtils.hasText(categoryId) ? categoryId : "";
    }

    private record RankedMockQuestion(PracticeQuestionDefinition question, int wrongCount) {
    }
}
