package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import com.leojcl.recruitmentsystem.entity.JobSeekerSave;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    public List<JobSeekerSave> findByJob(JobPostActivity job);

    @Transactional
    @Modifying
    @Query("delete from JobSeekerSave s where s.job = :job")
    void deleteByJob(@Param("job") JobPostActivity job);
}
