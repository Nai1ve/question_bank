package com.onepass.practice.practice;

import com.onepass.practice.auth.StudentIdentityService;
import com.onepass.practice.common.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/practice")
public class PracticeController {

    private static final Logger log = LoggerFactory.getLogger(PracticeController.class);

    private final StudentIdentityService studentIdentityService;
    private final PracticeService practiceService;

    public PracticeController(
            StudentIdentityService studentIdentityService,
            PracticeService practiceService
    ) {
        this.studentIdentityService = studentIdentityService;
        this.practiceService = practiceService;
    }

    @PostMapping("/sessions")
    public ApiResponse<PracticeStartResponse> startPracticeSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody PracticeStartRequest request
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeStartResponse response = practiceService.startPracticeSession(studentId, request);
        log.info(
                "Started practice session studentId={} entryType={} categoryId={} questionCount={} feedbackMode={} ok={} sessionId={} totalCount={}",
                studentId,
                request.entryType(),
                request.categoryId() == null ? "" : request.categoryId(),
                request.questionCount(),
                request.feedbackMode(),
                response.ok(),
                response.sessionId() == null ? "" : response.sessionId(),
                response.totalCount()
        );
        return ApiResponse.ok(response);
    }

    @GetMapping("/sessions/active")
    public ApiResponse<PracticeSessionView> findActiveSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String entryType,
            @RequestParam(required = false) String categoryId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeSessionView session = practiceService.findActiveSession(studentId, new PracticeActiveSessionQuery(entryType, categoryId));
        log.info(
                "Looked up active practice session studentId={} entryType={} categoryId={} found={}",
                studentId,
                entryType,
                categoryId == null ? "" : categoryId,
                session != null
        );
        return ApiResponse.ok(session);
    }

    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<PracticeSessionView> getPracticeSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeSessionView session = practiceService.getPracticeSession(studentId, sessionId);
        log.info(
                "Loaded practice session studentId={} sessionId={} status={} currentSequence={} totalCount={}",
                studentId,
                sessionId,
                session.status(),
                session.currentSequence(),
                session.totalCount()
        );
        return ApiResponse.ok(session);
    }

    @PostMapping("/sessions/{sessionId}/answers")
    public ApiResponse<PracticeAnswerSubmitResponse> submitPracticeAnswer(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId,
            @Valid @RequestBody PracticeAnswerSubmitRequest request
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeAnswerSubmitResponse response = practiceService.submitPracticeAnswer(studentId, sessionId, request);
        log.info(
                "Submitted practice answer studentId={} sessionId={} questionId={} selectedCount={} correct={}",
                studentId,
                sessionId,
                request.questionId(),
                request.selectedOptions() == null ? 0 : request.selectedOptions().size(),
                response.correct()
        );
        return ApiResponse.ok(response);
    }

    @PostMapping("/sessions/{sessionId}/next")
    public ApiResponse<PracticeSessionView> moveToNextQuestion(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeSessionView session = practiceService.moveToNextQuestion(studentId, sessionId);
        log.info(
                "Moved to next practice question studentId={} sessionId={} currentSequence={} totalCount={}",
                studentId,
                sessionId,
                session.currentSequence(),
                session.totalCount()
        );
        return ApiResponse.ok(session);
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ApiResponse<PracticeSummaryView> completePracticeSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeSummaryView summary = practiceService.completePracticeSession(studentId, sessionId);
        log.info(
                "Completed practice session studentId={} sessionId={} totalCount={} correctCount={} wrongCount={}",
                studentId,
                sessionId,
                summary.totalCount(),
                summary.correctCount(),
                summary.wrongCount()
        );
        return ApiResponse.ok(summary);
    }

    @PostMapping("/sessions/{sessionId}/abandon")
    public ApiResponse<Void> abandonPracticeSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        practiceService.abandonPracticeSession(studentId, sessionId);
        log.info("Abandoned practice session studentId={} sessionId={}", studentId, sessionId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/sessions/{sessionId}/summary")
    public ApiResponse<PracticeSummaryView> getPracticeSummary(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable String sessionId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        PracticeSummaryView summary = practiceService.getPracticeSummary(studentId, sessionId);
        log.info(
                "Loaded practice summary studentId={} sessionId={} totalCount={} correctCount={} wrongCount={}",
                studentId,
                sessionId,
                summary.totalCount(),
                summary.correctCount(),
                summary.wrongCount()
        );
        return ApiResponse.ok(summary);
    }
}
