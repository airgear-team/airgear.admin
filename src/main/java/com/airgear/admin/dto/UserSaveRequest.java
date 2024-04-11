package com.airgear.admin.dto;

import javax.validation.constraints.*;

public record UserSaveRequest(

        @Email(message = "email must be a valid email string")
        @NotNull(message = "email must not be null")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, message = "password's length must be at least 8")
        String password,

        @Pattern(regexp = "^\\+380\\d{9}$", message = "phones must be in the format +380XXXXXXXXX")
        String phone,

        @NotBlank(message = "name must not be blank")
        String name

) {
}
