package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerApply;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {
    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
