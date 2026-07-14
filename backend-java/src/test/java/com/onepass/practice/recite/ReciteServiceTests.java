package com.onepass.practice.recite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onepass.practice.recite.persistence.ReciteDayRecordDO;
import com.onepass.practice.recite.persistence.ReciteDayRecordMapper;
import com.onepass.practice.recite.persistence.RecitePlanDO;
import com.onepass.practice.recite.persistence.RecitePlanDayDO;
import com.onepass.practice.recite.persistence.RecitePlanDayMapper;
import com.onepass.practice.recite.persistence.RecitePlanMapper;
import com.onepass.practice.vocabulary.persistence.VocabularyBookMapper;
import com.onepass.practice.vocabulary.persistence.VocabularyWordDO;
import com.onepass.practice.vocabulary.persistence.VocabularyWordMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class ReciteServiceTests {

    private static final Long STUDENT_ID = 1001L;
    private static final Long PLAN_ID = 1L;
    private static final Long PLAN_DAY_ID = 10L;

    private RecitePlanMapper recitePlanMapper;
    private RecitePlanDayMapper recitePlanDayMapper;
    private ReciteDayRecordMapper reciteDayRecordMapper;
    private VocabularyBookMapper vocabularyBookMapper;
    private VocabularyWordMapper vocabularyWordMapper;
    private ReciteService reciteService;

    @BeforeEach
    void setUp() {
        recitePlanMapper = mock(RecitePlanMapper.class);
        recitePlanDayMapper = mock(RecitePlanDayMapper.class);
        reciteDayRecordMapper = mock(ReciteDayRecordMapper.class);
        vocabularyBookMapper = mock(VocabularyBookMapper.class);
        vocabularyWordMapper = mock(VocabularyWordMapper.class);
        reciteService = new ReciteService(
                provider(RecitePlanMapper.class, recitePlanMapper),
                provider(RecitePlanDayMapper.class, recitePlanDayMapper),
                provider(ReciteDayRecordMapper.class, reciteDayRecordMapper),
                provider(VocabularyBookMapper.class, vocabularyBookMapper),
                provider(VocabularyWordMapper.class, vocabularyWordMapper),
                new ObjectMapper()
        );
    }

    @Test
    void listPlanDaysTreatsSingleModeRecordAsPendingTest() {
        RecitePlanDO plan = plan();
        RecitePlanDayDO day = day("COMPLETED");
        ReciteDayRecordDO cnToEnRecord = record(201L, ReciteMode.CN_TO_EN.value());

        when(recitePlanMapper.selectByIdAndStudentId(PLAN_ID, STUDENT_ID)).thenReturn(plan);
        when(recitePlanDayMapper.selectByPlanId(PLAN_ID)).thenReturn(List.of(day));
        when(reciteDayRecordMapper.selectLatestByPlanDayIdAndStudentId(PLAN_DAY_ID, STUDENT_ID)).thenReturn(cnToEnRecord);
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.CN_TO_EN.value()))
                .thenReturn(cnToEnRecord);
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.EN_TO_CN.value()))
                .thenReturn(null);

        RecitePlanDaysResponse response = reciteService.listPlanDays(STUDENT_ID, PLAN_ID);

        RecitePlanDayView dayView = response.days().get(0);
        assertEquals("PENDING_TEST", dayView.status());
        assertTrue(dayView.cnToEnCompleted());
        assertFalse(dayView.enToCnCompleted());
        assertEquals(201L, dayView.cnToEnRecordId());
        assertEquals(0, response.plan().completedDays());
        assertEquals("Day 1", response.plan().currentDayLabel());
    }

    @Test
    void submitSingleModeKeepsDayPending() {
        stubSubmitBase();
        ReciteDayRecordDO cnToEnRecord = record(301L, ReciteMode.CN_TO_EN.value());
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.CN_TO_EN.value()))
                .thenReturn(cnToEnRecord);
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.EN_TO_CN.value()))
                .thenReturn(null);

        ReciteSubmitResponse response = reciteService.submit(
                STUDENT_ID,
                PLAN_ID,
                1,
                new ReciteSubmitRequest(ReciteMode.CN_TO_EN.value(), List.of(new ReciteAnswerItemRequest(101L, "abandon")))
        );

        assertEquals(301L, response.recordId());
        verify(recitePlanDayMapper).updateLatestResult(
                eq(PLAN_DAY_ID),
                eq("PENDING"),
                eq("100%"),
                eq(1),
                eq(0),
                any(LocalDateTime.class)
        );
        verify(recitePlanDayMapper, never()).updateCompletion(anyLong(), anyString(), any(), any(), any(), any());
    }

    @Test
    void submitSecondModeCompletesDay() {
        stubSubmitBase();
        ReciteDayRecordDO cnToEnRecord = record(301L, ReciteMode.CN_TO_EN.value());
        ReciteDayRecordDO enToCnRecord = record(302L, ReciteMode.EN_TO_CN.value());
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.CN_TO_EN.value()))
                .thenReturn(cnToEnRecord);
        when(reciteDayRecordMapper.selectLatestByPlanDayIdStudentIdAndMode(PLAN_DAY_ID, STUDENT_ID, ReciteMode.EN_TO_CN.value()))
                .thenReturn(enToCnRecord);

        ReciteSubmitResponse response = reciteService.submit(
                STUDENT_ID,
                PLAN_ID,
                1,
                new ReciteSubmitRequest(ReciteMode.EN_TO_CN.value(), List.of(new ReciteAnswerItemRequest(101L, "放弃")))
        );

        assertEquals(302L, response.recordId());
        verify(recitePlanDayMapper).updateCompletion(
                eq(PLAN_DAY_ID),
                eq("COMPLETED"),
                eq("100%"),
                eq(1),
                eq(0),
                any(LocalDateTime.class)
        );
        verify(recitePlanDayMapper, never()).updateLatestResult(anyLong(), anyString(), any(), any(), any(), any());
    }

    private void stubSubmitBase() {
        when(recitePlanMapper.selectByIdAndStudentId(PLAN_ID, STUDENT_ID)).thenReturn(plan());
        when(recitePlanDayMapper.selectByPlanIdAndDayNumber(PLAN_ID, 1)).thenReturn(day("PENDING"));
        when(vocabularyWordMapper.selectByBookIdAndSortRange("book-1", 1, 1)).thenReturn(List.of(word()));
        when(reciteDayRecordMapper.insert(any(ReciteDayRecordDO.class))).thenAnswer(invocation -> {
            ReciteDayRecordDO item = invocation.getArgument(0);
            item.setId(ReciteMode.EN_TO_CN.value().equals(item.getMode()) ? 302L : 301L);
            return 1;
        });
    }

    private static RecitePlanDO plan() {
        RecitePlanDO plan = new RecitePlanDO();
        plan.setId(PLAN_ID);
        plan.setStudentId(STUDENT_ID);
        plan.setBookId("book-1");
        plan.setBookName("测试词库");
        plan.setDailyCount(1);
        plan.setTotalWords(1);
        plan.setTotalDays(1);
        plan.setStatus("ACTIVE");
        return plan;
    }

    private static RecitePlanDayDO day(String status) {
        RecitePlanDayDO day = new RecitePlanDayDO();
        day.setId(PLAN_DAY_ID);
        day.setPlanId(PLAN_ID);
        day.setDayNumber(1);
        day.setDayLabel("Day 1");
        day.setStartWordOrder(1);
        day.setEndWordOrder(1);
        day.setTotalCount(1);
        day.setStatus(status);
        day.setStudyCompletedAt(LocalDateTime.now());
        return day;
    }

    private static ReciteDayRecordDO record(Long id, String mode) {
        ReciteDayRecordDO record = new ReciteDayRecordDO();
        record.setId(id);
        record.setPlanDayId(PLAN_DAY_ID);
        record.setPlanId(PLAN_ID);
        record.setStudentId(STUDENT_ID);
        record.setBookName("测试词库");
        record.setDayLabel("Day 1");
        record.setMode(mode);
        record.setTotalCount(1);
        record.setCorrectCount(1);
        record.setWrongCount(0);
        record.setAccuracy("100%");
        record.setAnswersJson("[]");
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    private static VocabularyWordDO word() {
        VocabularyWordDO word = new VocabularyWordDO();
        word.setId(101L);
        word.setBookId("book-1");
        word.setEnglish("abandon");
        word.setChinese("放弃");
        word.setPartOfSpeech("v.");
        word.setSortOrder(1);
        return word;
    }

    private static <T> ObjectProvider<T> provider(Class<T> type, T bean) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton(type.getName(), bean);
        return beanFactory.getBeanProvider(type);
    }
}
