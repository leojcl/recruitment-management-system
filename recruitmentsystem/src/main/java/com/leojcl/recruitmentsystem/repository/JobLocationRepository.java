package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLocationRepository extends JpaRepository<JobLocation, Integer> {
}
