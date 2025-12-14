package com.leojcl.recruitmentsystem.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Integer userId;
    private String email;
    private String displayName;
    private String role;
    private boolean authenticated;
}
