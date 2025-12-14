package com.leojcl.recruitmentsystem.dto.job;

import com.leojcl.recruitmentsystem.dto.common.CompanyDto;
import com.leojcl.recruitmentsystem.dto.common.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCardDto {
    private Integer jobPostId;
    private String jobTitle;
    private CompanyDto company;
    private LocationDto location;
    private String jobType; // Part-Time | Full-Time | Freelance
    private String remote;  // Remote-Only | Office-Only | Partial-Remote
    private String salary;
    private Date postedDate;
    private boolean applied; // đã apply?
    private boolean saved;   // đã lưu?
    private Long totalCandidates; // cho recruiter dashboard
}
