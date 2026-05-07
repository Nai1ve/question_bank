package com.onepass.practice.practice;

import com.onepass.practice.common.AppException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InMemoryPracticeSessionStore implements PracticeSessionStore {

    private final Map<String, PracticeSessionAggregate> sessions = new ConcurrentHashMap<>();
    private final Map<String, QuestionWrongStatSnapshot> questionStats = new ConcurrentHashMap<>();

    public InMemoryPracticeSessionStore(QuestionWrongStatSeedCatalog questionWrongStatSeedCatalog) {
        for (QuestionWrongStatSeed seed : questionWrongStatSeedCatalog.listAll()) {
            questionStats.put(
                    key(seed.studentId(), seed.questionId()),
                    new QuestionWrongStatSnapshot(seed.questionId(), seed.answeredCount(), seed.wrongCount())
            );
        }
    }

    @Override
    public PracticeSessionAggregate findActiveSession(long studentId, String entryType, String categoryId) {
        return sessions.values().stream()
                .filter(session -> session.getStudentId() == studentId)
                .filter(session -> session.getStatus() == PracticeSessionStatus.ONGOING)
                .filter(session -> entryType.equals(session.getEntryType()))
                .filter(session -> categoryId.equals(session.getCategoryId()))
                .sorted((left, right) -> right.getLastActiveAt().compareTo(left.getLastActiveAt()))
                .map(PracticeSessionAggregate::copy)
                .findFirst()
                .orElse(null);
    }

    @Override
    public PracticeSessionAggregate findSession(long studentId, String sessionId) {
        PracticeSessionAggregate session = sessions.get(sessionId);
        if (session == null || session.getStudentId() != studentId) {
            return null;
        }
        return session.copy();
    }

    @Override
    public void createSession(PracticeSessionAggregate session) {
        PracticeSessionAggregate previous = sessions.putIfAbsent(session.getSessionId(), session.copy());
        if (previous != null) {
            throw new AppException("Practice session already exists");
        }
    }

    @Override
    public void saveSessionProgress(PracticeSessionAggregate session) {
        updateSession(
                session,
                stored -> {
                    PracticeSessionAggregate updated = session.copy();
                    updated.setVersion(session.getVersion() + 1);
                    return updated;
                }
        );
        session.setVersion(session.getVersion() + 1);
    }

    @Override
    public void saveQuestionAnswer(PracticeSessionAggregate session, PracticeQuestionSnapshot question) {
        updateSession(
                session,
                stored -> {
                    PracticeQuestionSnapshot storedQuestion = stored.findQuestionById(question.getQuestionId());
                    if (storedQuestion == null) {
                        throw new AppException("Practice question does not exist");
                    }
                    if (storedQuestion.getVersion() != question.getVersion()) {
                        throw new AppException("Practice question snapshot update failed");
                    }

                    PracticeSessionAggregate updated = stored.copy();
                    PracticeQuestionSnapshot updatedQuestion = updated.findQuestionById(question.getQuestionId());
                    updatedQuestion.setUserAnswer(new java.util.ArrayList<>(question.getUserAnswer()));
                    updatedQuestion.setUserAnswerLabel(question.getUserAnswerLabel());
                    updatedQuestion.setAnswerLabel(question.getAnswerLabel());
                    updatedQuestion.setSubmitted(question.isSubmitted());
                    updatedQuestion.setCorrect(question.isCorrect());
                    updatedQuestion.setVersion(question.getVersion() + 1);
                    return updated;
                }
        );
        question.setVersion(question.getVersion() + 1);
    }

    @Override
    public void completeSession(PracticeSessionAggregate session) {
        updateSession(
                session,
                stored -> {
                    PracticeSessionAggregate updated = session.copy();
                    updated.setVersion(session.getVersion() + 1);
                    return updated;
                }
        );

        for (PracticeQuestionSnapshot question : session.getQuestions()) {
            questionStats.compute(
                    key(session.getStudentId(), question.getQuestionId()),
                    (ignored, current) -> {
                        QuestionWrongStatSnapshot snapshot = current == null
                                ? new QuestionWrongStatSnapshot(question.getQuestionId(), 0, 0)
                                : current;
                        return new QuestionWrongStatSnapshot(
                                question.getQuestionId(),
                                snapshot.answeredCount() + 1,
                                snapshot.wrongCount() + (question.isCorrect() ? 0 : 1)
                        );
                    }
            );
        }

        session.setVersion(session.getVersion() + 1);
    }

    @Override
    public void markSessionAbandoned(PracticeSessionAggregate session) {
        saveSessionProgress(session);
    }

    @Override
    public void markSessionExpired(PracticeSessionAggregate session) {
        saveSessionProgress(session);
    }

    @Override
    public Set<String> listAnsweredQuestionIds(long studentId, List<String> questionIds) {
        Set<String> answered = new HashSet<>();
        for (String questionId : questionIds) {
            QuestionWrongStatSnapshot stat = questionStats.get(key(studentId, questionId));
            if (stat != null && stat.answeredCount() > 0) {
                answered.add(questionId);
            }
        }
        return answered;
    }

    @Override
    public Map<String, QuestionWrongStatSnapshot> getQuestionStats(long studentId, List<String> questionIds) {
        Map<String, QuestionWrongStatSnapshot> result = new HashMap<>();
        for (String questionId : questionIds) {
            QuestionWrongStatSnapshot stat = questionStats.get(key(studentId, questionId));
            if (stat != null) {
                result.put(questionId, stat);
            }
        }
        return result;
    }

    private String key(long studentId, String questionId) {
        return studentId + ":" + questionId;
    }

    private void updateSession(
            PracticeSessionAggregate session,
            Function<PracticeSessionAggregate, PracticeSessionAggregate> updater
    ) {
        sessions.compute(session.getSessionId(), (sessionId, stored) -> {
            if (stored == null) {
                throw new AppException("Practice session does not exist");
            }
            if (stored.getVersion() != session.getVersion()) {
                throw new AppException("Practice session update failed");
            }
            return updater.apply(stored);
        });
    }
}
