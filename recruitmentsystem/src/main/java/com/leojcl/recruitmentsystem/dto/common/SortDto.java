package com.leojcl.recruitmentsystem.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {
    @NotBlank
    @Size(max = 50)
    private String sortBy; // e.g. postedDate, salary, jobTitle

    @NotBlank
    @Pattern(regexp = "ASC|DESC", message = "direction phải là ASC hoặc DESC")
    private String direction;
}
