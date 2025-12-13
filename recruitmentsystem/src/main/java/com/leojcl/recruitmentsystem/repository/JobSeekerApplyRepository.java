package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerApply;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {
    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

    boolean existsByUserIdAndJob(JobSeekerProfile userId, JobPostActivity job);

    @Transactional
    @Modifying
    @Query("delete from JobSeekerApply a where a.job = :job")
    void deleteByJob(@Param("job") JobPostActivity job);
}
