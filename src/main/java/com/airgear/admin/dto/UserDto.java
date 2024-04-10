package com.airgear.admin.dto;

import com.airgear.admin.model.Role;
import com.airgear.admin.model.User;
import com.airgear.admin.model.UserStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record UserDto(Long id,
                      String email,
                      String phone,
                      String name,
                      Set<Role> roles,
                      Long countGoods,
                      OffsetDateTime createdAt,
                      OffsetDateTime deleteAt,
                      OffsetDateTime lastActivity,
                      UserStatus status,
                      Float rating) {

    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getRoles(),
                null,
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                user.getStatus(),
                user.getRating()
                );
    }

    public static List<UserDto> fromUsers(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        users.forEach(user -> result.add(UserDto.fromUser(user)));
        return result;
    }
}
