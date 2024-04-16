package jikgong.domain.history.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class HistoryAtFinishResponse {
    private List<HistoryManageResponse> workMemberResponse;
    private List<HistoryManageResponse> notWorkMemberResponse;
}
