package com.airgear.admin.dto;

public record UserCountResponse(Long count) {

    public static UserCountResponse fromCount(Long count) {
        return new UserCountResponse(count);
    }
}
