package com.airgear.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUserDto {

    private String username;
    private String password;
}
