package jikgong.domain.wage.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class WageSaveRequest {
    @Schema(description = "일급", example = "130000")
    private Integer dailyWage; // 일급
    @Schema(description = "메모", example = "추가 근무 1시간")
    private String memo; // 메모
    @Schema(description = "회사 명", example = "(주)직공")
    private String companyName; // 회사 명
    @Schema(description = "근무 시작 시간", example = "2023-12-25T09:30:00.000Z")
    private LocalDateTime startTime; // 근무 시작 시간
    @Schema(description = "근무 시작 시간", example = "2023-12-25T18:00:00.000Z")
    private LocalDateTime endTime; // 근무 종료 시간
}
