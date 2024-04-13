package com.airgear.admin.controller;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.dto.UserDto;
import com.airgear.admin.exception.BadDataException;
import com.airgear.admin.dto.UserResponse;
import com.airgear.admin.service.UserService;
import com.airgear.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication auth) {
        return ResponseEntity.ok(userService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count/new")
    public ResponseEntity<CountDto> getCountOfNewUsers(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                              @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return ResponseEntity.ok().body(userService.getCountOfNewUsers(fromDate, toDate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count/deleted")
    public ResponseEntity<CountDto> getCountOfDeletedUsers(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                                         @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return ResponseEntity.ok().body(userService.getCountOfDeletedUsers(fromDate, toDate));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/goods/count")
    public Page<CountByNameDto> getUserGoodsCount(@RequestParam(required = false, defaultValue = "30") int limit) {
        if (limit > Constants.MAX_LIMIT) {
            throw new BadDataException("Limit exceeds maximum value of "+Constants.MAX_LIMIT+"!");
        }
        Pageable pageable = PageRequest.of(0, limit);

        return userService.getUserGoodsCount(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public Page<UserDto> searchUsers(@RequestParam(value = "search") String search,
                                                     @RequestParam(required = false, defaultValue = "30") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        return  userService.searchUsers(search,pageable);
    }

}
