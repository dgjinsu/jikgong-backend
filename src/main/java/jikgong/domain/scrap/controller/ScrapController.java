package jikgong.domain.scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import jikgong.domain.jobPost.dtos.worker.JobPostListResponse;
import jikgong.domain.scrap.service.ScrapService;
import jikgong.global.dto.Response;
import jikgong.global.security.principal.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScrapController {
    private final ScrapService scrapService;

    @Operation(summary = "모집 공고 스크랩 or 취소")
    @PostMapping("/api/scrap/{jobPostId}")
    public ResponseEntity<Response> saveScrap(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @PathVariable("jobPostId") Long jobPostId) {
        Boolean result = scrapService.saveScrap(principalDetails.getMember().getId(), jobPostId);
        String message = result ? "완료" : "취소";
        return ResponseEntity.ok(new Response("모집 공고 스크랩 " + message));
    }

    @Operation(summary = "스크랩한 모집 공고 조회")
    @GetMapping("/api/scrap/job-posts")
    public ResponseEntity<Response> findScrapJobPostList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate")));
        Page<JobPostListResponse> jobPostListResponsePage = scrapService.findScrapJobPostList(principalDetails.getMember().getId(), pageable);
        return ResponseEntity.ok(new Response(jobPostListResponsePage, "스크랩한 모집 공고 조회 완료"));
    }

}
