package com.leojcl.recruitmentsystem.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto {
    @NotNull
    @Min(value = 0, message = "Trang bắt đầu từ 0")
    private Integer page;

    @NotNull
    @Min(value = 1, message = "Kích thước trang tối thiểu là 1")
    @Max(value = 100, message = "Kích thước trang tối đa là 100")
    private Integer size;
}
