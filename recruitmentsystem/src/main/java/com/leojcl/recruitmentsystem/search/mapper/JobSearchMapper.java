package com.leojcl.recruitmentsystem.search.mapper;

import com.leojcl.recruitmentsystem.entity.JobPostActivity;
import com.leojcl.recruitmentsystem.search.document.JobSearchDoc;

public class JobSearchMapper {

    public static JobSearchDoc toDoc(JobPostActivity e) {
        JobSearchDoc d = new JobSearchDoc();

        d.setJobPostId(e.getJobPostId());
        d.setJobTitle(e.getJobTitle());
        d.setDescriptionOfJob(e.getDescriptionOfJob());
        d.setJobType(e.getJobType());
        d.setRemote(e.getRemote());
        d.setSalary(e.getSalary());
        d.setPostedDate(e.getPostedDate());

        if (e.getJobCompanyId() != null) {
            d.setCompanyName(e.getJobCompanyId().getName());
        }

        if (e.getJobLocationId() != null) {
            d.setLocationCity(e.getJobLocationId().getCity());
        }

        return d;
    }
}
