package com.airgear.admin.service;

import com.airgear.admin.dto.*;
import com.airgear.model.Role;
import com.airgear.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserService {

    UserResponse createAdmin(UserSaveRequest request);

    Page<UserResponse> list(Pageable pageable);

    Page<UserSearchResponse> findUsers(String name, String email, String phone, UserStatus status,
                                       OffsetDateTime createdAt, OffsetDateTime deletedAt, Pageable pageable);

    Optional<UserResponse> findById(long id);

    UserCountResponse getCountOfNewUsers(OffsetDateTime fromDate, OffsetDateTime toDate);

    UserCountResponse getCountOfDeletedUsers(OffsetDateTime fromDate, OffsetDateTime toDate);

    Page<UserCountByNameResponse> getCountOfUserGoods(Pageable pageable);

    UserResponse mergeById(long id, UserMergeRequest request);

    UserResponse changeStatusById(long id, UserStatus status);

    UserResponse changePasswordById(long id, UserOverridePasswordRequest request);

    UserResponse appointRole(long ide, Role role);

    UserResponse cancelRole(long id, Role role);

    void deleteById(long id);

}
