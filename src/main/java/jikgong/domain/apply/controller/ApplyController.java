package jikgong.domain.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.apply.dtos.company.ApplyResponseForCompany;
import jikgong.domain.apply.dtos.company.ApplyProcessRequest;
import jikgong.domain.apply.dtos.worker.ApplyPendingResponse;
import jikgong.domain.apply.dtos.worker.ApplyResponseForWorker;
import jikgong.domain.apply.dtos.worker.ApplyResponseMonthly;
import jikgong.domain.apply.dtos.worker.ApplySaveRequest;
import jikgong.domain.apply.service.ApplyCompanyService;
import jikgong.domain.apply.service.ApplyWorkerService;
import jikgong.domain.member.dtos.MemberAcceptedResponse;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApplyController {
    private final ApplyWorkerService applyWorkerService;
    private final ApplyCompanyService applyCompanyService;

    @Operation(summary = "노동자: 일자리 신청")
    @PostMapping("/api/apply")
    public ResponseEntity<Response> saveApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @RequestBody ApplySaveRequest request) {
        applyWorkerService.saveApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("일자리 신청 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 일별 조회", description = "workDate: 2024-01-01")
    @GetMapping("/api/apply/worker")
    public ResponseEntity<Response> findAcceptedApplyWorker(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                           @RequestParam("workDate") LocalDate workDate) {
        List<ApplyResponseForWorker> applyResponseList = applyWorkerService.findAcceptedApplyWorker(principalDetails.getMember().getId(), workDate);
        return ResponseEntity.ok(new Response(applyResponseList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 월별 조회", description = "신청 내역 확정 화면의 달력 동그라미 표시할 날짜 반환  \n workMonth: 2024-01-01  << 이렇게 주면 년,월만 추출 예정")
    @GetMapping("/api/apply/worker/monthly")
    public ResponseEntity<Response> findAcceptedApplyWorkerMonthly(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                   @RequestParam("workMonth") LocalDate workMonth) {
        List<ApplyResponseMonthly> applyResponseMonthlyList = applyWorkerService.findAcceptedApplyWorkerMonthly(principalDetails.getMember().getId(), workMonth);
        return ResponseEntity.ok(new Response(applyResponseMonthlyList, "일자리 신청 내역 조회 완료"));
    }

    @Operation(summary = "노동자: 신청 내역 조회 - 진행 중")
    @GetMapping("/api/apply/worker/pending")
    public ResponseEntity<Response> findPendingApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (먼저 요청한 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));
        Page<ApplyPendingResponse> pendingApplyPage = applyWorkerService.findPendingApply(principalDetails.getMember().getId(), pageable);
        return ResponseEntity.ok(new Response(pendingApplyPage, "일자리 신청 내역 조회 완료"));
    }

    // todo: 일자리 신청 취소 프로세스 확정 안 남
    // todo: 기한이 지났을 경우 자동으로 신청 취소되게 하는 기능도 개발해야 함

    @Operation(summary = "인력 관리: 대기 중인 노동자 조회")
    @GetMapping("/api/apply/company/pending/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findPendingApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @PathVariable("jobPostId") Long jobPostId,
                                                            @PathVariable("workDateId") Long workDateId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (먼저 요청한 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdDate")));
        Page<ApplyResponseForCompany> applyResponseForCompanyPage =
                applyCompanyService.findPendingApplyHistoryCompany(principalDetails.getMember().getId(), jobPostId, workDateId, pageable);
        return ResponseEntity.ok(new Response(applyResponseForCompanyPage, "공고 글에 신청된 내역 조회 완료"));
    }

    @Operation(summary = "인력 관리: 확정된 노동자 조회")
    @GetMapping("/api/apply/company/accepted/{jobPostId}/{workDateId}")
    public ResponseEntity<Response> findAcceptedApplyCompany(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                             @PathVariable("jobPostId") Long jobPostId,
                                                             @PathVariable("workDateId") Long workDateId,
                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        // 페이징 처리 (이름 순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("m.workerInfo.workerName")));
        Page<ApplyResponseForCompany> applyResponseForCompanyPage =
                applyCompanyService.findAcceptedHistoryCompany(principalDetails.getMember().getId(), jobPostId, workDateId, pageable);
        return ResponseEntity.ok(new Response(applyResponseForCompanyPage, "공고 글에 확정된 노동자 조회"));
    }


    @Operation(summary = "인력 관리: 인부 요청 처리")
    @PostMapping("/api/apply/company/process-request")
    public ResponseEntity<Response> processApply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestBody ApplyProcessRequest request) {
        applyCompanyService.processApply(principalDetails.getMember().getId(), request);
        return ResponseEntity.ok(new Response("인부 요청 처리 완료"));
    }
}
