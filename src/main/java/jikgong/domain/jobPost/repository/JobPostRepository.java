package jikgong.domain.jobPost.repository;

import jikgong.domain.jobPost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("select j from JobPost j where j.member.id = :memberId and j.isTemporary = true")
    List<JobPost> findTemporaryJobPostByMemberId(@Param("memberId") Long memberId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.endDate < :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findCompletedJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate < :now and j.endDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findInProgressJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findPlannedJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId);
}
