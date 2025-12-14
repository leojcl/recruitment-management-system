package com.leojcl.recruitmentsystem.dto.job;

import com.leojcl.recruitmentsystem.dto.common.CompanyDto;
import com.leojcl.recruitmentsystem.dto.common.LocationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobPostUpdateFormDto {
    @NotNull(message = "JobPostId là bắt buộc")
    private Integer jobPostId;

    @Size(max = 150, message = "Tiêu đề tối đa 150 ký tự")
    private String jobTitle;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String descriptionOfJob;

    @Pattern(regexp = "Part-Time|Full-Time|Freelance", message = "Loại công việc phải là Part-Time, Full-Time hoặc Freelance")
    private String jobType;

    @Size(max = 50, message = "Lương tối đa 50 ký tự")
    private String salary;

    @Pattern(regexp = "Remote-Only|Office-Only|Partial-Remote", message = "Hình thức làm việc phải là Remote-Only, Office-Only hoặc Partial-Remote")
    private String remote;

    private Integer jobLocationId;
    private Integer jobCompanyId;

    @Valid
    private LocationDto location;

    @Valid
    private CompanyDto company;
}
