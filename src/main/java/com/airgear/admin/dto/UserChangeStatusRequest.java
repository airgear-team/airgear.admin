package com.airgear.admin.dto;

import com.airgear.model.UserStatus;

import javax.validation.constraints.NotNull;

public record UserChangeStatusRequest(

        @NotNull(message = "user status must not be null")
        UserStatus status

) {
}
