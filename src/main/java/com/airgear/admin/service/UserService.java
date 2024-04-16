package com.airgear.admin.service;

import com.airgear.admin.dto.*;
import com.airgear.admin.model.Role;
import com.airgear.admin.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserService {

    UserResponse createAdmin(UserSaveRequest request);

    Page<UserResponse> list(Pageable pageable);

    Optional<UserResponse> findById(long id);

    Optional<UserResponse> findByEmail(String email);

    UserCountResponse getCountOfNewUsers(OffsetDateTime fromDate, OffsetDateTime toDate);

    UserCountResponse getCountOfDeletedUsers(OffsetDateTime fromDate, OffsetDateTime toDate);

    Page<UserCountByNameResponse> getCountOfUserGoods(Pageable pageable);

    UserResponse mergeById(long id, UserMergeRequest request);

    UserResponse mergeByEmail(String email, UserMergeRequest request);

    UserResponse changeStatusById(long id, UserStatus status);

    UserResponse changePasswordById(long id, UserOverridePasswordRequest request);

    UserResponse changePasswordByEmail(String email, UserChangePasswordRequest request);

    UserResponse appointRole(long ide, Role role);

    UserResponse cancelRole(long id, Role role);

    void deleteById(long id);

    void deleteByEmail(String email);

    Page<UserSearchResponse> searchUsers(String search, Pageable pageable);
}
