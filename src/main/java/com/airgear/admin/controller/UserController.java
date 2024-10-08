package com.airgear.admin.controller;

import com.airgear.admin.dto.*;
import com.airgear.admin.exception.UserExceptions;
import com.airgear.admin.service.UserService;
import com.airgear.model.Role;
import com.airgear.model.UserStatus;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.OffsetDateTime;

import static com.airgear.admin.utils.Routes.USERS;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //region authenticated user API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PageableAsQueryParam
    public Page<UserResponse> listUsers(@Parameter(hidden = true) Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping(
            value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PageableAsQueryParam
    public Page<UserSearchResponse> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime deletedAt,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return userService.findUsers(name, email, phone, status, createdAt, deletedAt, pageable);
    }

    //endregion

    //region admin-only API

    @GetMapping(
            value = "/count/new",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountResponse getCountOfNewUsers(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate
    ) {
        return userService.getCountOfNewUsers(fromDate, toDate);
    }

    @GetMapping(
            value = "/count/deleted",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountResponse getCountOfDeletedUsers(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate
    ) {
        return userService.getCountOfDeletedUsers(fromDate, toDate);
    }

    @GetMapping(
            value = "/count/goods",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<UserCountByNameResponse> getCountOfUserGoods(@Parameter(hidden = true) Pageable pageable) {
        return userService.getCountOfUserGoods(pageable);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerAdmin(@RequestBody @Valid UserSaveRequest request) {
        return userService.createAdmin(request);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse getUserById(@PathVariable long id) {
        return userService.findById(id)
                .orElseThrow(() -> UserExceptions.userNotFound(id));
    }

    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse mergeUserById(@PathVariable long id,
                                      @RequestBody @Valid UserMergeRequest request) {
        return userService.mergeById(id, request);
    }

    @PatchMapping(
            value = "/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse changeUserStatusById(@PathVariable long id,
                                             @RequestBody @Valid UserChangeStatusRequest request) {
        return userService.changeStatusById(id, request.status());
    }

    @PatchMapping(
            value = "/{id}/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse changeUserPassword(@PathVariable long id,
                                           @RequestBody @Valid UserOverridePasswordRequest request) {
        return userService.changePasswordById(id, request);
    }

    @PatchMapping(
            value = "/{id}/appoint",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse appointRoleById(@PathVariable long id,
                                        @RequestBody Role role) {
        return userService.appointRole(id, role);
    }

    @PatchMapping(
            value = "/{id}/cancel",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse cancelRoleById(@PathVariable long id,
                                       @RequestBody Role role) {
        return userService.cancelRole(id, role);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long id) {
        userService.deleteById(id);
    }

    //endregion
}
