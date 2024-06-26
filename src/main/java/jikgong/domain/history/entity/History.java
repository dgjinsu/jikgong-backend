package jikgong.domain.history.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class History extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private WorkStatus startStatus; // 출근, 결근
    private LocalDateTime startStatusDecisionTime; // 출근, 결근 처리 시간

    @Enumerated(EnumType.STRING)
    private WorkStatus endStatus; // 퇴근, 조퇴
    private LocalDateTime endStatusDecisionTime; // 퇴근, 조퇴 처리 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_date_id")
    private WorkDate workDate;

    @Builder
    public History(WorkStatus startStatus, LocalDateTime startStatusDecisionTime, WorkStatus endStatus,
        LocalDateTime endStatusDecisionTime, Member member, WorkDate workDate) {
        this.startStatus = startStatus;
        this.startStatusDecisionTime = startStatusDecisionTime;
        this.endStatus = endStatus;
        this.endStatusDecisionTime = endStatusDecisionTime;
        this.member = member;
        this.workDate = workDate;
    }

    public static History createEntity(WorkStatus workStatus, LocalDateTime now, Member member, WorkDate workDate) {
        return History.builder()
            .startStatus(workStatus)
            .startStatusDecisionTime(now)
            .member(member)
            .workDate(workDate)
            .build();
    }
}
