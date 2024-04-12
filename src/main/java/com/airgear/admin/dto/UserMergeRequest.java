package com.airgear.admin.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record UserMergeRequest(

        @Email(message = "email must be a valid email string")
        String email,

        @Pattern(regexp = "^\\+380\\d{9}$", message = "phone must be in the format +380XXXXXXXXX")
        String phone,

        @NotBlank(message = "name must not be blank")
        String name

) {
}
