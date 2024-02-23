package jikgong.domain.headHunting.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.headHunting.dtos.company.HeadHuntingListResponse;
import jikgong.domain.headHunting.dtos.company.offer.OfferJobPostRequest;
import jikgong.domain.headHunting.dtos.company.offer.OfferRequest;
import jikgong.domain.headHunting.dtos.company.offer.SelectOfferJobPostResponse;
import jikgong.domain.headHunting.dtos.company.WorkerInfoResponse;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.headHunting.repository.HeadHuntingRepository;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.service.NotificationService;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.global.event.dtos.NotificationEvent;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeadHuntingCompanyService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final HeadHuntingRepository headHuntingRepository;
    private final ApplyRepository applyRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher publisher;
    private final JobPostRepository jobPostRepository;

    public Page<HeadHuntingListResponse> findHeadHuntingList(Long memberId, Long projectId, Tech tech, Float bound, SortType sortType, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        return headHuntingRepository.findHeadHuntingMemberList(project.getAddress(), tech, bound, sortType, pageable);
    }

    public void offerJobPost(Long companyId, OfferRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        HeadHunting headHunting = headHuntingRepository.findByIdWithMember(request.getHeadHuntingId())
                .orElseThrow(() -> new CustomException(ErrorCode.HEAD_HUNTING_NOT_FOUND));

        // 요청한 jobPostId 가 유효한지 체크
        List<Long> jobPostIdList = request.getOfferJobPostRequest().stream()
                .map(OfferJobPostRequest::getJobPostId)
                .collect(Collectors.toList());
        Long findJobPostCount = jobPostRepository.findByIdList(jobPostIdList);
        if (jobPostIdList.size() != findJobPostCount) {
            throw new CustomException(ErrorCode.JOB_POST_NOT_FOUND);
        }

        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {

            publisher.publishEvent(new NotificationEvent(company.getCompanyInfo().getCompanyName(), offerJobPostRequest.getWorkDateList(), offerJobPostRequest.getJobPostId(), NotificationType.OFFER, headHunting.getMember().getId()));
        }
    }

    public WorkerInfoResponse findWorkerInfo(Long companyId, Long headHuntingId, LocalDate selectMonth) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        HeadHunting headHunting = headHuntingRepository.findByIdWithMember(headHuntingId)
                .orElseThrow(() -> new CustomException(ErrorCode.HEAD_HUNTING_NOT_FOUND));

        List<Apply> findCantWorkDate = applyRepository.findCantWorkDate(
                headHunting.getMember().getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        return WorkerInfoResponse.from(headHunting, findCantWorkDate);
    }

    public SelectOfferJobPostResponse findAvailableJobPosts(Long companyId, Long workerId, Long projectId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));


        List<LocalDate> cantWorkDate = applyRepository.findAllCantWorkDate(worker.getId()).stream()
                .map(apply -> apply.getWorkDate().getWorkDate())
                .collect(Collectors.toList());
        Set<LocalDate> cantWorkDateSet = new HashSet<>(cantWorkDate);

        List<JobPost> jobPostList = jobPostRepository.findByProject(project.getId());

        return SelectOfferJobPostResponse.from(jobPostList, worker, cantWorkDateSet);
    }
}
