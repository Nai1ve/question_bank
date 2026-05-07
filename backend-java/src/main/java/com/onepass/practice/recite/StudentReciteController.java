package com.onepass.practice.recite;

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
@RequestMapping("/api/student/recite")
public class StudentReciteController {

    private static final Logger log = LoggerFactory.getLogger(StudentReciteController.class);

    private final StudentIdentityService studentIdentityService;
    private final ReciteService reciteService;

    public StudentReciteController(
            StudentIdentityService studentIdentityService,
            ReciteService reciteService
    ) {
        this.studentIdentityService = studentIdentityService;
        this.reciteService = reciteService;
    }

    @GetMapping("/plans/active")
    public ApiResponse<ReciteActivePlanView> getActivePlan(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        ReciteActivePlanView plan = reciteService.getActivePlan(studentId);
        log.info("Loaded active recite plan studentId={} found={}", studentId, plan != null);
        return ApiResponse.ok(plan);
    }

    @PostMapping("/plans")
    public ApiResponse<RecitePlanCreateResponse> createPlan(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody RecitePlanCreateRequest request
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        RecitePlanCreateResponse response = reciteService.createPlan(studentId, request);
        log.info("Created recite plan studentId={} planId={} bookId={} dailyCount={}",
                studentId, response.planId(), response.bookId(), response.dailyCount());
        return ApiResponse.ok(response);
    }

    @GetMapping("/plans/{planId}/days")
    public ApiResponse<RecitePlanDaysResponse> listPlanDays(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long planId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        RecitePlanDaysResponse response = reciteService.listPlanDays(studentId, planId);
        log.info("Listed recite plan days studentId={} planId={} count={}",
                studentId, planId, response.days().size());
        return ApiResponse.ok(response);
    }

    @GetMapping("/plans/{planId}/days/{dayNumber}/study")
    public ApiResponse<ReciteStudyView> getStudy(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long planId,
            @PathVariable Integer dayNumber
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        ReciteStudyView response = reciteService.getStudy(studentId, planId, dayNumber);
        log.info("Loaded recite study studentId={} planId={} dayNumber={} totalCount={}",
                studentId, planId, dayNumber, response.totalCount());
        return ApiResponse.ok(response);
    }

    @PostMapping("/plans/{planId}/days/{dayNumber}/study-complete")
    public ApiResponse<Void> completeStudy(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long planId,
            @PathVariable Integer dayNumber
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        reciteService.completeStudy(studentId, planId, dayNumber);
        log.info("Completed recite study studentId={} planId={} dayNumber={}",
                studentId, planId, dayNumber);
        return ApiResponse.ok(null);
    }

    @GetMapping("/plans/{planId}/days/{dayNumber}")
    public ApiResponse<ReciteSessionView> getSession(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long planId,
            @PathVariable Integer dayNumber,
            @RequestParam String mode
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        ReciteSessionView response = reciteService.getSession(studentId, planId, dayNumber, mode);
        log.info("Loaded recite session studentId={} planId={} dayNumber={} mode={} totalCount={}",
                studentId, planId, dayNumber, mode, response.totalCount());
        return ApiResponse.ok(response);
    }

    @PostMapping("/plans/{planId}/days/{dayNumber}/submit")
    public ApiResponse<ReciteSubmitResponse> submit(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long planId,
            @PathVariable Integer dayNumber,
            @Valid @RequestBody ReciteSubmitRequest request
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        ReciteSubmitResponse response = reciteService.submit(studentId, planId, dayNumber, request);
        log.info("Submitted recite session studentId={} planId={} dayNumber={} recordId={} accuracy={}",
                studentId, planId, dayNumber, response.recordId(), response.accuracy());
        return ApiResponse.ok(response);
    }

    @GetMapping("/records/{recordId}")
    public ApiResponse<ReciteSummaryView> getSummary(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long recordId
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        ReciteSummaryView response = reciteService.getSummary(studentId, recordId);
        log.info("Loaded recite summary studentId={} recordId={} totalCount={}",
                studentId, recordId, response.totalCount());
        return ApiResponse.ok(response);
    }
}
