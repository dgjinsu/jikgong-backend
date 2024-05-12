package jikgong.domain.jobPost.dtos.worker;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostMapResponse {
    private Long jobPostId;
    private Tech tech; // 직종
    private Float latitude; // 위도
    private Float longitude; // 경도

    public static JobPostMapResponse from(JobPost jobPost) {
        return JobPostMapResponse.builder()
                .jobPostId(jobPost.getId())
                .tech(jobPost.getTech())
                .latitude(jobPost.getAddress().getLatitude())
                .longitude(jobPost.getAddress().getLongitude())
                .build();
    }
}