package com.leojcl.recruitmentsystem.dto.job;

import com.leojcl.recruitmentsystem.dto.common.PaginationDto;
import com.leojcl.recruitmentsystem.dto.common.SortDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobSearchRequestDto {
    @Size(max = 100, message = "Từ khóa tối đa 100 ký tự")
    private String keyword; // tìm theo tiêu đề/mô tả

    @Pattern(regexp = "Part-Time|Full-Time|Freelance|", message = "Loại công việc không hợp lệ")
    private String jobType; // cho phép rỗng

    @Pattern(regexp = "Remote-Only|Office-Only|Partial-Remote|", message = "Hình thức làm việc không hợp lệ")
    private String remote; // cho phép rỗng

    private Integer locationId;
    private Integer companyId;

    @Valid
    private PaginationDto page = new PaginationDto(0, 10);

    @Valid
    private SortDto sort = new SortDto("postedDate", "DESC");
}
