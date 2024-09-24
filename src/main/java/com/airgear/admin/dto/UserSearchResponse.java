package com.airgear.admin.dto;

import com.airgear.model.Role;
import com.airgear.model.User;
import com.airgear.model.UserStatus;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Set;

public record UserSearchResponse(Long id,
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

    public static UserSearchResponse fromUser(User user) {
        return new UserSearchResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                EnumSet.copyOf(user.getRoles()),
                (long) user.getGoods().size(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                user.getStatus(),
                user.getRating()
        );
    }
}
