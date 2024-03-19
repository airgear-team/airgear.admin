package com.airgear.admin.controller;

import com.airgear.admin.model.User;
import com.airgear.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<User> getAllUsers(Authentication auth) {
        log.info("auth name : {}", auth.getName());
        return userService.findAll();
    }

}
