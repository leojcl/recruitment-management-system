package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.JobCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCompanyRepository extends JpaRepository<JobCompany, Integer> {
}
