package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.repository.JobPostActivityRepository;
import com.leojcl.recruitmentsystem.search.document.JobSearchDoc;
import com.leojcl.recruitmentsystem.search.mapper.JobSearchMapper;
import com.leojcl.recruitmentsystem.search.repository.JobSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/search")
@RequiredArgsConstructor
public class AdminSearchController {

    private final JobPostActivityRepository jobPostActivityRepository;
    private final JobSearchRepository jobSearchRepository;
    

    @PostMapping("/reindex")
    public String reindexAllJobs() {
        List<JobPostActivity> jobs = jobPostActivityRepository.findAll();
        List<JobSearchDoc> docs = jobs.stream().map(JobSearchMapper::toDoc).toList();

        jobSearchRepository.saveAll(docs);

        return "Reindex completed: " + docs.size();
    }
}
