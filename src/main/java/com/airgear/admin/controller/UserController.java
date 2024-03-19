package com.airgear.admin.controller;

import com.airgear.admin.response.UserResponse;
import com.airgear.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
