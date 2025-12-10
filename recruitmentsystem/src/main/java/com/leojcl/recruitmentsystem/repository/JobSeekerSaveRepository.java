package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    public List<JobSeekerSave> findByJob(JobPostActivity job);

}
