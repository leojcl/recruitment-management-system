package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
    @EntityGraph(attributePaths = "skills")
    Optional<JobSeekerProfile> findWithSkillsByUserAccountId(Integer userAccountId);
}
