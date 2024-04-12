package com.airgear.admin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UserChangePasswordRequest(

        @NotNull(message = "old password must not be null")
        String oldPassword,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, message = "password's length must be at least 8")
        String newPassword

) {
}
