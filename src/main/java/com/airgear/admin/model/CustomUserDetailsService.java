package com.airgear.admin.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class CustomUserDetailsService extends org.springframework.security.core.userdetails.User {

    public CustomUserDetailsService(User source) {
        super(
                source.getEmail(),
                source.getPassword(),
                source.getStatus().equals(UserStatus.ACTIVE),
                true,
                true,
                true,
                getAuthorities(source)
        );
    }

    private static Set<SimpleGrantedAuthority> getAuthorities(User source) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        source.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getValue())));
        return authorities;
    }
}
