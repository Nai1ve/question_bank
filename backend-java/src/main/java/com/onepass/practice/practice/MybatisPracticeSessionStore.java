package com.onepass.practice.practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onepass.practice.common.AppException;
import com.onepass.practice.practice.persistence.PracticeAnswerRecordDO;
import com.onepass.practice.practice.persistence.PracticeAnswerRecordMapper;
import com.onepass.practice.practice.persistence.PracticeSessionDO;
import com.onepass.practice.practice.persistence.PracticeSessionMapper;
import com.onepass.practice.practice.persistence.PracticeSessionQuestionDO;
import com.onepass.practice.practice.persistence.PracticeSessionQuestionMapper;
import com.onepass.practice.practice.persistence.QuestionWrongStatDO;
import com.onepass.practice.practice.persistence.QuestionWrongStatMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class MybatisPracticeSessionStore implements PracticeSessionStore {

    private final PracticeSessionMapper practiceSessionMapper;
    private final PracticeSessionQuestionMapper practiceSessionQuestionMapper;
    private final PracticeAnswerRecordMapper practiceAnswerRecordMapper;
    private final QuestionWrongStatMapper questionWrongStatMapper;
    private final ObjectMapper objectMapper;

    public MybatisPracticeSessionStore(
            PracticeSessionMapper practiceSessionMapper,
            PracticeSessionQuestionMapper practiceSessionQuestionMapper,
            PracticeAnswerRecordMapper practiceAnswerRecordMapper,
            QuestionWrongStatMapper questionWrongStatMapper,
            ObjectMapper objectMapper
    ) {
        this.practiceSessionMapper = practiceSessionMapper;
        this.practiceSessionQuestionMapper = practiceSessionQuestionMapper;
        this.practiceAnswerRecordMapper = practiceAnswerRecordMapper;
        this.questionWrongStatMapper = questionWrongStatMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public PracticeSessionAggregate findActiveSession(long studentId, String entryType, String categoryId) {
        PracticeSessionDO sessionDO = practiceSessionMapper.selectLatestActiveSession(studentId, entryType, categoryId);
        if (sessionDO == null) {
            return null;
        }
        return loadSessionAggregate(sessionDO);
    }

    @Override
    public PracticeSessionAggregate findSession(long studentId, String sessionId) {
        PracticeSessionDO sessionDO = practiceSessionMapper.selectBySessionId(sessionId);
        if (sessionDO == null || !studentIdEquals(sessionDO.getStudentId(), studentId)) {
            return null;
        }
        return loadSessionAggregate(sessionDO);
    }

    @Override
    public void createSession(PracticeSessionAggregate session) {
        PracticeSessionDO sessionDO = toSessionDO(session);
        practiceSessionMapper.insert(sessionDO);

        List<PracticeSessionQuestionDO> questionItems = session.getQuestions().stream()
                .map(question -> toQuestionDO(session.getSessionId(), question))
                .toList();
        if (!questionItems.isEmpty()) {
            practiceSessionQuestionMapper.batchInsert(questionItems);
        }
    }

    @Override
    public void saveSessionProgress(PracticeSessionAggregate session) {
        int affected = practiceSessionMapper.updateProgressAndActivity(
                session.getSessionId(),
                session.getCurrentIndex(),
                toLocalDateTime(session.getLastActiveAt()),
                session.getVersion()
        );
        assertAffected(affected, "Practice session progress update failed");
        session.setVersion(session.getVersion() + 1);
    }

    @Override
    public void saveQuestionAnswer(PracticeSessionAggregate session, PracticeQuestionSnapshot question) {
        int affected = practiceSessionQuestionMapper.updateAnswerSnapshot(
                session.getSessionId(),
                question.getQuestionId(),
                writeJson(question.getUserAnswer()),
                question.getUserAnswerLabel(),
                question.getAnswerLabel(),
                question.isSubmitted(),
                question.isCorrect(),
                question.getVersion()
        );
        assertAffected(affected, "Practice question snapshot update failed");
        question.setVersion(question.getVersion() + 1);

        Integer maxSubmitSeq = practiceAnswerRecordMapper.selectMaxSubmitSeq(session.getSessionId(), question.getQuestionId());
        PracticeAnswerRecordDO record = new PracticeAnswerRecordDO();
        record.setSessionId(session.getSessionId());
        record.setQuestionId(question.getQuestionId());
        record.setSubmitSeq((maxSubmitSeq == null ? 0 : maxSubmitSeq) + 1);
        record.setSelectedAnswerJson(writeJson(question.getUserAnswer()));
        record.setCorrect(question.isCorrect());
        record.setSubmittedAt(toLocalDateTime(session.getLastActiveAt()));
        practiceAnswerRecordMapper.insert(record);
    }

    @Override
    public void completeSession(PracticeSessionAggregate session) {
        int affected = practiceSessionMapper.updateStatus(
                session.getSessionId(),
                session.getStatus().name(),
                toLocalDateTime(session.getLastActiveAt()),
                toLocalDateTime(session.getCompletedAt()),
                toLocalDateTime(session.getExpiredAt()),
                toLocalDateTime(session.getAbandonedAt()),
                session.getVersion()
        );
        assertAffected(affected, "Practice session completion update failed");
        session.setVersion(session.getVersion() + 1);

        for (PracticeQuestionSnapshot question : session.getQuestions()) {
            questionWrongStatMapper.upsertIncrement(
                    session.getStudentId(),
                    question.getQuestionId(),
                    1,
                    question.isCorrect() ? 0 : 1,
                    toLocalDateTime(session.getCompletedAt()),
                    question.isCorrect() ? null : toLocalDateTime(session.getCompletedAt())
            );
        }
    }

    @Override
    public void markSessionAbandoned(PracticeSessionAggregate session) {
        updateSessionStatus(session, "Practice session abandon update failed");
    }

    @Override
    public void markSessionExpired(PracticeSessionAggregate session) {
        updateSessionStatus(session, "Practice session expiration update failed");
    }

    @Override
    public Set<String> listAnsweredQuestionIds(long studentId, List<String> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Set.of();
        }

        return loadQuestionStats(studentId, questionIds).stream()
                .filter(stat -> stat.getAnsweredCount() != null && stat.getAnsweredCount() > 0)
                .map(QuestionWrongStatDO::getQuestionId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, QuestionWrongStatSnapshot> getQuestionStats(long studentId, List<String> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Map.of();
        }

        Map<String, QuestionWrongStatSnapshot> result = new HashMap<>();
        for (QuestionWrongStatDO stat : loadQuestionStats(studentId, questionIds)) {
            if (stat != null) {
                result.put(
                        stat.getQuestionId(),
                        new QuestionWrongStatSnapshot(
                                stat.getQuestionId(),
                                stat.getAnsweredCount() == null ? 0 : stat.getAnsweredCount(),
                                stat.getWrongCount() == null ? 0 : stat.getWrongCount()
                        )
                );
            }
        }
        return result;
    }

    private List<QuestionWrongStatDO> loadQuestionStats(long studentId, List<String> questionIds) {
        return questionWrongStatMapper.selectByStudentAndQuestionIds(studentId, questionIds);
    }

    private void updateSessionStatus(PracticeSessionAggregate session, String failureMessage) {
        int affected = practiceSessionMapper.updateStatus(
                session.getSessionId(),
                session.getStatus().name(),
                toLocalDateTime(session.getLastActiveAt()),
                toLocalDateTime(session.getCompletedAt()),
                toLocalDateTime(session.getExpiredAt()),
                toLocalDateTime(session.getAbandonedAt()),
                session.getVersion()
        );
        assertAffected(affected, failureMessage);
        session.setVersion(session.getVersion() + 1);
    }

    private PracticeSessionAggregate loadSessionAggregate(PracticeSessionDO sessionDO) {
        List<PracticeSessionQuestionDO> questionDOs = practiceSessionQuestionMapper.selectBySessionId(sessionDO.getSessionId());
        PracticeSessionAggregate aggregate = new PracticeSessionAggregate();
        aggregate.setSessionId(sessionDO.getSessionId());
        aggregate.setStudentId(sessionDO.getStudentId());
        aggregate.setEntryType(sessionDO.getEntryType());
        aggregate.setCategoryId(sessionDO.getCategoryId());
        aggregate.setCategoryName(sessionDO.getCategoryName());
        aggregate.setFeedbackMode(sessionDO.getFeedbackMode());
        aggregate.setStatus(PracticeSessionStatus.valueOf(sessionDO.getStatus()));
        aggregate.setCurrentIndex(sessionDO.getCurrentIndex() == null ? 0 : sessionDO.getCurrentIndex());
        aggregate.setStartedAt(toInstant(sessionDO.getStartedAt()));
        aggregate.setLastActiveAt(toInstant(sessionDO.getLastActiveAt()));
        aggregate.setCompletedAt(toInstant(sessionDO.getCompletedAt()));
        aggregate.setExpiredAt(toInstant(sessionDO.getExpiredAt()));
        aggregate.setAbandonedAt(toInstant(sessionDO.getAbandonedAt()));
        aggregate.setVersion(sessionDO.getVersion() == null ? 0 : sessionDO.getVersion());
        aggregate.setQuestions(questionDOs.stream().map(this::toQuestionSnapshot).collect(Collectors.toCollection(ArrayList::new)));
        return aggregate;
    }

    private PracticeSessionDO toSessionDO(PracticeSessionAggregate session) {
        PracticeSessionDO result = new PracticeSessionDO();
        result.setSessionId(session.getSessionId());
        result.setStudentId(session.getStudentId());
        result.setEntryType(session.getEntryType());
        result.setCategoryId(session.getCategoryId());
        result.setCategoryName(session.getCategoryName());
        result.setFeedbackMode(session.getFeedbackMode());
        result.setStatus(session.getStatus().name());
        result.setCurrentIndex(session.getCurrentIndex());
        result.setTotalCount(session.getTotalCount());
        result.setStartedAt(toLocalDateTime(session.getStartedAt()));
        result.setLastActiveAt(toLocalDateTime(session.getLastActiveAt()));
        result.setCompletedAt(toLocalDateTime(session.getCompletedAt()));
        result.setExpiredAt(toLocalDateTime(session.getExpiredAt()));
        result.setAbandonedAt(toLocalDateTime(session.getAbandonedAt()));
        result.setVersion(session.getVersion());
        return result;
    }

    private PracticeSessionQuestionDO toQuestionDO(String sessionId, PracticeQuestionSnapshot question) {
        PracticeSessionQuestionDO result = new PracticeSessionQuestionDO();
        result.setSessionId(sessionId);
        result.setQuestionId(question.getQuestionId());
        result.setQuestionOrder(question.getOrder());
        result.setQuestionType(question.getType());
        result.setStem(question.getStem());
        result.setTagsJson(writeJson(question.getTags()));
        result.setOptionsJson(writeJson(question.getOptions()));
        result.setStandardAnswerJson(writeJson(question.getStandardAnswer()));
        result.setAnalysis(question.getAnalysis());
        result.setUserAnswerJson(writeJson(question.getUserAnswer()));
        result.setUserAnswerLabel(question.getUserAnswerLabel());
        result.setAnswerLabel(question.getAnswerLabel());
        result.setSubmitted(question.isSubmitted());
        result.setCorrect(question.isCorrect());
        result.setVersion(question.getVersion());
        return result;
    }

    private PracticeQuestionSnapshot toQuestionSnapshot(PracticeSessionQuestionDO questionDO) {
        PracticeQuestionSnapshot snapshot = new PracticeQuestionSnapshot();
        snapshot.setQuestionId(questionDO.getQuestionId());
        snapshot.setOrder(questionDO.getQuestionOrder() == null ? 0 : questionDO.getQuestionOrder());
        snapshot.setType(questionDO.getQuestionType());
        snapshot.setStem(questionDO.getStem());
        snapshot.setTags(readStringList(questionDO.getTagsJson()));
        snapshot.setOptions(readOptions(questionDO.getOptionsJson()));
        snapshot.setStandardAnswer(readStringList(questionDO.getStandardAnswerJson()));
        snapshot.setAnalysis(questionDO.getAnalysis());
        snapshot.setUserAnswer(readStringList(questionDO.getUserAnswerJson()));
        snapshot.setUserAnswerLabel(questionDO.getUserAnswerLabel() == null ? "" : questionDO.getUserAnswerLabel());
        snapshot.setAnswerLabel(questionDO.getAnswerLabel() == null ? "" : questionDO.getAnswerLabel());
        snapshot.setSubmitted(Boolean.TRUE.equals(questionDO.getSubmitted()));
        snapshot.setCorrect(Boolean.TRUE.equals(questionDO.getCorrect()));
        snapshot.setVersion(questionDO.getVersion() == null ? 0 : questionDO.getVersion());
        return snapshot;
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException exception) {
            throw new AppException("Failed to deserialize practice string list");
        }
    }

    private List<PracticeQuestionOptionView> readOptions(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<PracticeQuestionOptionView>>() {
            });
        } catch (JsonProcessingException exception) {
            throw new AppException("Failed to deserialize practice option list");
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new AppException("Failed to serialize practice snapshot");
        }
    }

    private void assertAffected(int affectedRows, String message) {
        if (affectedRows <= 0) {
            throw new AppException(message);
        }
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toInstant(ZoneOffset.UTC);
    }

    private boolean studentIdEquals(Long value, long studentId) {
        return value != null && value == studentId;
    }
}
