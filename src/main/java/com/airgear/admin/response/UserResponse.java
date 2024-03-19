package com.airgear.admin.response;

import com.airgear.admin.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record UserResponse(Long id,
                           String username,
                           String email,
                           String phone,
                           String name,
                           Set<RoleResponse> roles) {

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                RoleResponse.fromRoles(user.getRoles()));
    }

    public static List<UserResponse> fromUsers(List<User> users) {
        List<UserResponse> result = new ArrayList<>();
        users.forEach(user -> result.add(UserResponse.fromUser(user)));
        return result;
    }
}
