package com.leojcl.recruitmentsystem.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Integer id; // optional when create new company inline

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 150, message = "Tên công ty tối đa 150 ký tự")
    private String name;

    @Size(max = 255, message = "Đường dẫn logo tối đa 255 ký tự")
    private String logo;
}
