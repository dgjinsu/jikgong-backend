package jikgong.domain.wage.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.wage.entity.WageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
public class WageModifyRequest {
    @Schema(description = "wageId", example = "1")
    private Long wageId;
    @Schema(description = "일급", example = "130000")
    private Integer dailyWage; // 일급
    @Schema(description = "현장명", example = "사하구 낙동5블럭 낙동강 온도 측정 센서")
    private String title; // 현장명
    @Schema(description = "근무 날짜", example = "2024-01-01", type="string")
    private LocalDate workDate; // 근무 날짜
    @Schema(description = "근무 시작 시간", example = "09:30:00", type="string")
    private LocalTime startTime; // 근무 시작 시간
    @Schema(description = "근무 종료 시간", example = "18:00:00", type="string")
    private LocalTime endTime; // 근무 종료 시간
    @Schema(description = "직종", example = "NORMAL")
    private Tech tech; // 직종
    @Schema(description = "입력 타입", example = "CUSTOM")
    private WageType wageType; // 입력 타입
}
