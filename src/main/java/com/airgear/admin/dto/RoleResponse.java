package com.airgear.admin.dto;

import com.airgear.admin.model.Role;

import java.util.HashSet;
import java.util.Set;

public record RoleResponse(String name) {

    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.name());
    }

    public static Set<RoleResponse> fromRoles(Set<Role> roles) {
        Set<RoleResponse> result = new HashSet<>();
        roles.forEach(role -> result.add(RoleResponse.fromRole(role)));
        return result;
    }
}
