package com.leojcl.recruitmentsystem.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Integer id; // optional when referencing existing location

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100, message = "Thành phố tối đa 100 ký tự")
    private String city;

    @Size(max = 100, message = "Bang/Tỉnh tối đa 100 ký tự")
    private String state;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
    private String country;
}
