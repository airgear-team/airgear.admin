package com.airgear.admin.dto;

import com.airgear.model.Role;
import com.airgear.model.UserStatus;

import java.time.OffsetDateTime;
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
}
