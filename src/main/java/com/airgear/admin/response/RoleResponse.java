package com.airgear.admin.response;

import com.airgear.admin.model.Role;

import java.util.HashSet;
import java.util.Set;

public record RoleResponse(Long id,
                           String name,
                           String description) {
    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getDescription());
    }

    public static Set<RoleResponse> fromRoles(Set<Role> roles) {
        Set<RoleResponse> result = new HashSet<>();
        roles.forEach(role -> result.add(RoleResponse.fromRole(role)));
        return result;
    }
}
