package com.leojcl.recruitmentsystem.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterFormDto {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu từ 6-100 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Xác nhận mật khẩu từ 6-100 ký tự")
    private String confirmPassword;

    @NotNull(message = "Loại người dùng là bắt buộc")
    private Integer userTypeId; // map sang UsersType

    @AssertTrue(message = "Mật khẩu và xác nhận mật khẩu không khớp")
    public boolean isPasswordConfirmed() {
        if (password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }
}
