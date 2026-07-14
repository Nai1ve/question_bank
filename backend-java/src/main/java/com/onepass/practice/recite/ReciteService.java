package com.onepass.practice.recite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onepass.practice.common.AppException;
import com.onepass.practice.recite.persistence.ReciteDayRecordDO;
import com.onepass.practice.recite.persistence.ReciteDayRecordMapper;
import com.onepass.practice.recite.persistence.RecitePlanDO;
import com.onepass.practice.recite.persistence.RecitePlanDayDO;
import com.onepass.practice.recite.persistence.RecitePlanDayMapper;
import com.onepass.practice.recite.persistence.RecitePlanMapper;
import com.onepass.practice.vocabulary.persistence.VocabularyBookDO;
import com.onepass.practice.vocabulary.persistence.VocabularyBookMapper;
import com.onepass.practice.vocabulary.persistence.VocabularyWordDO;
import com.onepass.practice.vocabulary.persistence.VocabularyWordMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ReciteService {

    private final RecitePlanMapper recitePlanMapper;
    private final RecitePlanDayMapper recitePlanDayMapper;
    private final ReciteDayRecordMapper reciteDayRecordMapper;
    private final VocabularyBookMapper vocabularyBookMapper;
    private final VocabularyWordMapper vocabularyWordMapper;
    private final ObjectMapper objectMapper;

    public ReciteService(
            ObjectProvider<RecitePlanMapper> recitePlanMapperProvider,
            ObjectProvider<RecitePlanDayMapper> recitePlanDayMapperProvider,
            ObjectProvider<ReciteDayRecordMapper> reciteDayRecordMapperProvider,
            ObjectProvider<VocabularyBookMapper> vocabularyBookMapperProvider,
            ObjectProvider<VocabularyWordMapper> vocabularyWordMapperProvider,
            ObjectMapper objectMapper
    ) {
        this.recitePlanMapper = recitePlanMapperProvider.getIfAvailable();
        this.recitePlanDayMapper = recitePlanDayMapperProvider.getIfAvailable();
        this.reciteDayRecordMapper = reciteDayRecordMapperProvider.getIfAvailable();
        this.vocabularyBookMapper = vocabularyBookMapperProvider.getIfAvailable();
        this.vocabularyWordMapper = vocabularyWordMapperProvider.getIfAvailable();
        this.objectMapper = objectMapper;
    }

    public ReciteActivePlanView getActivePlan(Long studentId) {
        requirePersistence();
        RecitePlanDO plan = recitePlanMapper.selectActiveByStudentId(studentId);
        return plan == null ? null : toActivePlanView(studentId, plan);
    }

    public RecitePlanCreateResponse createPlan(Long studentId, RecitePlanCreateRequest request) {
        requirePersistence();
        VocabularyBookDO book = requireBook(request.bookId());
        List<VocabularyWordDO> words = vocabularyWordMapper.selectByBookId(book.getId());
        if (words.isEmpty()) {
            throw new AppException("当前词库暂无可用单词");
        }

        recitePlanMapper.supersedeActivePlans(studentId, LocalDateTime.now());

        int dailyCount = request.dailyCount() == null ? 5 : request.dailyCount();
        int totalWords = words.size();
        int totalDays = (int) Math.ceil(totalWords * 1.0 / dailyCount);

        RecitePlanDO plan = new RecitePlanDO();
        plan.setStudentId(studentId);
        plan.setBookId(book.getId());
        plan.setBookName(book.getName());
        plan.setDailyCount(dailyCount);
        plan.setTotalWords(totalWords);
        plan.setTotalDays(totalDays);
        plan.setStatus("ACTIVE");
        recitePlanMapper.insert(plan);

        for (int day = 0; day < totalDays; day += 1) {
            int startIndex = day * dailyCount;
            int endIndex = Math.min(startIndex + dailyCount - 1, totalWords - 1);
            RecitePlanDayDO dayDO = new RecitePlanDayDO();
            dayDO.setPlanId(plan.getId());
            dayDO.setDayNumber(day + 1);
            dayDO.setDayLabel("Day " + (day + 1));
            dayDO.setStartWordOrder(words.get(startIndex).getSortOrder());
            dayDO.setEndWordOrder(words.get(endIndex).getSortOrder());
            dayDO.setTotalCount(endIndex - startIndex + 1);
            dayDO.setStatus("PENDING");
            recitePlanDayMapper.insert(dayDO);
        }

        return new RecitePlanCreateResponse(plan.getId(), plan.getBookId(), plan.getBookName(), plan.getDailyCount(), plan.getTotalDays());
    }

    public RecitePlanDaysResponse listPlanDays(Long studentId, Long planId) {
        requirePersistence();
        RecitePlanDO plan = requirePlan(studentId, planId);
        List<RecitePlanDayView> days = recitePlanDayMapper.selectByPlanId(planId).stream()
                .sorted(Comparator.comparing(RecitePlanDayDO::getDayNumber))
                .map(item -> toDayView(studentId, item))
                .toList();
        return new RecitePlanDaysResponse(toActivePlanView(studentId, plan), days);
    }

    public ReciteStudyView getStudy(Long studentId, Long planId, Integer dayNumber) {
        requirePersistence();
        RecitePlanDO plan = requirePlan(studentId, planId);
        RecitePlanDayDO day = requirePlanDay(planId, dayNumber);
        List<VocabularyWordDO> words = vocabularyWordMapper.selectByBookIdAndSortRange(
                plan.getBookId(),
                day.getStartWordOrder(),
                day.getEndWordOrder()
        );

        List<ReciteStudyItemView> items = words.stream()
                .map(word -> new ReciteStudyItemView(
                        word.getId(),
                        word.getEnglish(),
                        word.getChinese(),
                        word.getPartOfSpeech()
                ))
                .toList();

        return new ReciteStudyView(
                plan.getId(),
                plan.getBookName(),
                day.getDayNumber() == null ? 0 : day.getDayNumber(),
                day.getDayLabel(),
                items.size(),
                items
        );
    }

    public void completeStudy(Long studentId, Long planId, Integer dayNumber) {
        requirePersistence();
        requirePlan(studentId, planId);
        RecitePlanDayDO day = requirePlanDay(planId, dayNumber);
        recitePlanDayMapper.updateStudyCompletedAt(day.getId(), LocalDateTime.now());
    }

    public ReciteSessionView getSession(Long studentId, Long planId, Integer dayNumber, String modeValue) {
        requirePersistence();
        RecitePlanDO plan = requirePlan(studentId, planId);
        RecitePlanDayDO day = requirePlanDay(planId, dayNumber);
        ReciteMode mode = ReciteMode.fromValue(modeValue);
        ReciteModeRecords modeRecords = loadModeRecords(studentId, day);
        if (day.getStudyCompletedAt() == null && !modeRecords.anyCompleted()) {
            throw new AppException("请先完成学习再开始测试");
        }
        List<VocabularyWordDO> words = vocabularyWordMapper.selectByBookIdAndSortRange(
                plan.getBookId(),
                day.getStartWordOrder(),
                day.getEndWordOrder()
        );

        List<ReciteSessionItemView> items = words.stream()
                .map(word -> new ReciteSessionItemView(
                        word.getId(),
                        mode == ReciteMode.CN_TO_EN ? word.getChinese() : word.getEnglish(),
                        word.getEnglish(),
                        word.getChinese(),
                        word.getPartOfSpeech()
                ))
                .toList();

        return new ReciteSessionView(
                plan.getId(),
                plan.getBookName(),
                day.getDayNumber(),
                day.getDayLabel(),
                mode.value(),
                items.size(),
                items
        );
    }

    public ReciteSubmitResponse submit(Long studentId, Long planId, Integer dayNumber, ReciteSubmitRequest request) {
        requirePersistence();
        RecitePlanDO plan = requirePlan(studentId, planId);
        RecitePlanDayDO day = requirePlanDay(planId, dayNumber);
        ReciteMode mode = ReciteMode.fromValue(request.mode());
        List<VocabularyWordDO> words = vocabularyWordMapper.selectByBookIdAndSortRange(
                plan.getBookId(),
                day.getStartWordOrder(),
                day.getEndWordOrder()
        );

        Map<Long, String> answerMap = new HashMap<>();
        if (request.answers() != null) {
            for (ReciteAnswerItemRequest item : request.answers()) {
                if (item != null && item.wordId() != null) {
                    answerMap.put(item.wordId(), item.value());
                }
            }
        }

        List<ReciteSummaryItemView> items = new ArrayList<>();
        int correctCount = 0;
        for (VocabularyWordDO word : words) {
            String userAnswer = safeTrim(answerMap.get(word.getId()));
            String standardAnswer = mode == ReciteMode.CN_TO_EN ? word.getEnglish() : word.getChinese();
            boolean correct = isCorrect(mode, userAnswer, standardAnswer);
            if (correct) {
                correctCount += 1;
            }
            items.add(new ReciteSummaryItemView(
                    word.getId(),
                    mode == ReciteMode.CN_TO_EN ? word.getChinese() : word.getEnglish(),
                    word.getEnglish(),
                    word.getChinese(),
                    word.getPartOfSpeech(),
                    userAnswer,
                    standardAnswer,
                    correct
            ));
        }

        int totalCount = items.size();
        int wrongCount = totalCount - correctCount;
        String accuracy = formatAccuracy(correctCount, totalCount);

        ReciteDayRecordDO record = new ReciteDayRecordDO();
        record.setPlanDayId(day.getId());
        record.setPlanId(plan.getId());
        record.setStudentId(studentId);
        record.setBookName(plan.getBookName());
        record.setDayLabel(day.getDayLabel());
        record.setMode(mode.value());
        record.setTotalCount(totalCount);
        record.setCorrectCount(correctCount);
        record.setWrongCount(wrongCount);
        record.setAccuracy(accuracy);
        record.setAnswersJson(writeJson(items));
        reciteDayRecordMapper.insert(record);

        LocalDateTime now = LocalDateTime.now();
        ReciteModeRecords modeRecords = loadModeRecords(studentId, day);
        if (modeRecords.fullyCompleted()) {
            recitePlanDayMapper.updateCompletion(
                    day.getId(),
                    "COMPLETED",
                    accuracy,
                    correctCount,
                    wrongCount,
                    now
            );
        } else {
            recitePlanDayMapper.updateLatestResult(
                    day.getId(),
                    "PENDING",
                    accuracy,
                    correctCount,
                    wrongCount,
                    now
            );
        }

        return new ReciteSubmitResponse(record.getId(), totalCount, correctCount, wrongCount, accuracy);
    }

    public ReciteSummaryView getSummary(Long studentId, Long recordId) {
        requirePersistence();
        ReciteDayRecordDO record = reciteDayRecordMapper.selectByIdAndStudentId(recordId, studentId);
        if (record == null) {
            throw new AppException("背诵记录不存在");
        }

        List<ReciteSummaryItemView> items = readSummaryItems(record.getAnswersJson());
        return new ReciteSummaryView(
                record.getId(),
                record.getBookName(),
                record.getDayLabel(),
                record.getMode(),
                record.getTotalCount() == null ? 0 : record.getTotalCount(),
                record.getCorrectCount() == null ? 0 : record.getCorrectCount(),
                record.getWrongCount() == null ? 0 : record.getWrongCount(),
                record.getAccuracy(),
                items
        );
    }

    public String resolveCurrentPlanLabel(Long studentId) {
        requirePersistence();
        RecitePlanDO plan = recitePlanMapper.selectActiveByStudentId(studentId);
        if (plan == null) {
            return "暂无背诵计划";
        }

        List<RecitePlanDayDO> days = recitePlanDayMapper.selectByPlanId(plan.getId());
        RecitePlanDayDO pendingDay = findFirstIncompleteDay(studentId, days);
        return plan.getBookName() + " · " + (pendingDay == null ? "已完成" : pendingDay.getDayLabel());
    }

    private ReciteActivePlanView toActivePlanView(Long studentId, RecitePlanDO plan) {
        List<RecitePlanDayDO> days = recitePlanDayMapper.selectByPlanId(plan.getId());
        int completedDays = 0;
        RecitePlanDayDO pendingDay = null;
        for (RecitePlanDayDO day : days.stream().sorted(Comparator.comparing(RecitePlanDayDO::getDayNumber)).toList()) {
            if (loadModeRecords(studentId, day).fullyCompleted()) {
                completedDays += 1;
            } else if (pendingDay == null) {
                pendingDay = day;
            }
        }

        return new ReciteActivePlanView(
                plan.getId(),
                plan.getBookId(),
                plan.getBookName(),
                plan.getDailyCount() == null ? 0 : plan.getDailyCount(),
                plan.getTotalWords() == null ? 0 : plan.getTotalWords(),
                plan.getTotalDays() == null ? 0 : plan.getTotalDays(),
                completedDays,
                pendingDay == null ? "已完成" : pendingDay.getDayLabel(),
                plan.getStatus()
        );
    }

    private RecitePlanDayView toDayView(Long studentId, RecitePlanDayDO item) {
        ReciteDayRecordDO latestRecord = reciteDayRecordMapper.selectLatestByPlanDayIdAndStudentId(item.getId(), studentId);
        ReciteModeRecords modeRecords = loadModeRecords(studentId, item);
        return new RecitePlanDayView(
                item.getDayNumber() == null ? 0 : item.getDayNumber(),
                item.getDayLabel(),
                item.getTotalCount() == null ? 0 : item.getTotalCount(),
                resolveDayStatus(item, modeRecords),
                item.getStudyCompletedAt() != null || modeRecords.anyCompleted(),
                latestRecord == null ? null : latestRecord.getId(),
                latestRecord == null ? null : latestRecord.getMode(),
                latestRecord == null ? item.getLastAccuracy() : latestRecord.getAccuracy(),
                latestRecord == null ? item.getLastCorrectCount() : latestRecord.getCorrectCount(),
                latestRecord == null ? item.getLastWrongCount() : latestRecord.getWrongCount(),
                modeRecords.cnToEnCompleted(),
                modeRecords.cnToEn() == null ? null : modeRecords.cnToEn().getId(),
                modeRecords.cnToEn() == null ? null : modeRecords.cnToEn().getAccuracy(),
                modeRecords.enToCnCompleted(),
                modeRecords.enToCn() == null ? null : modeRecords.enToCn().getId(),
                modeRecords.enToCn() == null ? null : modeRecords.enToCn().getAccuracy()
        );
    }

    private String resolveDayStatus(RecitePlanDayDO item, ReciteModeRecords modeRecords) {
        if (modeRecords.fullyCompleted()) {
            return "COMPLETED";
        }
        return item.getStudyCompletedAt() == null && !modeRecords.anyCompleted() ? "PENDING_STUDY" : "PENDING_TEST";
    }

    private RecitePlanDayDO findFirstIncompleteDay(Long studentId, List<RecitePlanDayDO> days) {
        return days.stream()
                .sorted(Comparator.comparing(RecitePlanDayDO::getDayNumber))
                .filter(day -> !loadModeRecords(studentId, day).fullyCompleted())
                .findFirst()
                .orElse(null);
    }

    private ReciteModeRecords loadModeRecords(Long studentId, RecitePlanDayDO day) {
        ReciteDayRecordDO cnToEn = reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(
                day.getId(),
                studentId,
                ReciteMode.CN_TO_EN.value()
        );
        ReciteDayRecordDO enToCn = reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(
                day.getId(),
                studentId,
                ReciteMode.EN_TO_CN.value()
        );
        return new ReciteModeRecords(cnToEn, enToCn);
    }

    private record ReciteModeRecords(
            ReciteDayRecordDO cnToEn,
            ReciteDayRecordDO enToCn
    ) {
        private boolean cnToEnCompleted() {
            return cnToEn != null;
        }

        private boolean enToCnCompleted() {
            return enToCn != null;
        }

        private boolean anyCompleted() {
            return cnToEnCompleted() || enToCnCompleted();
        }

        private boolean fullyCompleted() {
            return cnToEnCompleted() && enToCnCompleted();
        }
    }

    private VocabularyBookDO requireBook(String bookId) {
        VocabularyBookDO book = vocabularyBookMapper.selectById(bookId);
        if (book == null) {
            throw new AppException("词库不存在");
        }
        return book;
    }

    private RecitePlanDO requirePlan(Long studentId, Long planId) {
        RecitePlanDO plan = recitePlanMapper.selectByIdAndStudentId(planId, studentId);
        if (plan == null) {
            throw new AppException("背诵计划不存在");
        }
        return plan;
    }

    private RecitePlanDayDO requirePlanDay(Long planId, Integer dayNumber) {
        RecitePlanDayDO day = recitePlanDayMapper.selectByPlanIdAndDayNumber(planId, dayNumber);
        if (day == null) {
            throw new AppException("背诵 Day 不存在");
        }
        return day;
    }

    private boolean isCorrect(ReciteMode mode, String userAnswer, String standardAnswer) {
        if (mode == ReciteMode.CN_TO_EN) {
            return normalizeEnglish(userAnswer).equals(normalizeEnglish(standardAnswer));
        }
        return normalizeText(userAnswer).equals(normalizeText(standardAnswer));
    }

    private String normalizeEnglish(String value) {
        return safeTrim(value).toLowerCase();
    }

    private String normalizeText(String value) {
        return safeTrim(value).replaceAll("\\s+", "");
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatAccuracy(int correctCount, int totalCount) {
        if (totalCount <= 0) {
            return "0%";
        }
        return Math.round(correctCount * 100.0 / totalCount) + "%";
    }

    private List<ReciteSummaryItemView> readSummaryItems(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ReciteSummaryItemView>>() {
            });
        } catch (JsonProcessingException exception) {
            throw new AppException("Failed to deserialize recite summary");
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new AppException("Failed to serialize recite summary");
        }
    }

    private void requirePersistence() {
        if (recitePlanMapper == null
                || recitePlanDayMapper == null
                || reciteDayRecordMapper == null
                || vocabularyBookMapper == null
                || vocabularyWordMapper == null) {
            throw new AppException("Recite data is not available");
        }
    }
}
