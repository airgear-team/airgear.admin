package com.airgear.admin.service;

import com.airgear.admin.model.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    List<User> findAll();
}
