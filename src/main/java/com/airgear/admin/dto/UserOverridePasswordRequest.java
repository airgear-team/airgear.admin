package com.airgear.admin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record UserOverridePasswordRequest(

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, message = "password's length must be at least 8")
        String password

) {
}
