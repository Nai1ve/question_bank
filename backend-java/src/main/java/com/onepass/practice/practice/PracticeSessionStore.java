package com.onepass.practice.practice;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PracticeSessionStore {

    PracticeSessionAggregate findActiveSession(long studentId, String entryType, String categoryId);

    PracticeSessionAggregate findSession(long studentId, String sessionId);

    void createSession(PracticeSessionAggregate session);

    void saveSessionProgress(PracticeSessionAggregate session);

    void saveQuestionAnswer(PracticeSessionAggregate session, PracticeQuestionSnapshot question);

    void completeSession(PracticeSessionAggregate session);

    void markSessionAbandoned(PracticeSessionAggregate session);

    void markSessionExpired(PracticeSessionAggregate session);

    Set<String> listAnsweredQuestionIds(long studentId, List<String> questionIds);

    Map<String, QuestionWrongStatSnapshot> getQuestionStats(long studentId, List<String> questionIds);
}
