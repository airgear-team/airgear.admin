package com.airgear.admin.service;

import com.airgear.admin.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
}
