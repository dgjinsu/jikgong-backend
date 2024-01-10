package jikgong.domain.project.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class ProjectSaveRequest {
    private String name; // 프로젝트 명
    @Schema(description = "프로젝트 착공일", example = "2024-01-01")
    private LocalDate startDate; // 착공일
    @Schema(description = "프로젝트 준공일", example = "2024-03-01")
    private LocalDate endDate; // 준공일
    @Schema(description = "작업 장소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 작업 장소
}
