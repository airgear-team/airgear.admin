package com.airgear.admin.dto;

import com.airgear.admin.model.Role;
import com.airgear.admin.model.User;
import com.airgear.admin.model.UserStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record UserResponse(Long id,
                           String email,
                           String phone,
                           String name,
                           UserStatus status,
                           OffsetDateTime createdAt,
                           OffsetDateTime deleteAt,
                           OffsetDateTime lastActivity,
                           Set<Role> roles) {

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                EnumSet.copyOf(user.getRoles()));
    }

    public static UserResponse fromUserWithBasicAttributes(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                null);
    }

    public static List<UserResponse> fromUsers(List<User> users) {
        List<UserResponse> result = new ArrayList<>();
        users.forEach(user -> result.add(UserResponse.fromUser(user)));
        return result;
    }
}
