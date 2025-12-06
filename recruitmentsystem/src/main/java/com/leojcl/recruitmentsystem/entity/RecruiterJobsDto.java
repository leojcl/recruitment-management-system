package com.leojcl.recruitmentsystem.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterJobsDto {
    private Long totalCandidates;
    private Integer jobPostId;
    private String jobTitle;
    private JobLocation jobLocation;
    private JobCompany jobCompany;

    public RecruiterJobsDto() {
    }

    public RecruiterJobsDto(Long totalCandidates, Integer jobPostId, String jobTitle, JobLocation jobLocation, JobCompany jobCompany) {
        this.totalCandidates = totalCandidates;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobCompany = jobCompany;
    }
}
