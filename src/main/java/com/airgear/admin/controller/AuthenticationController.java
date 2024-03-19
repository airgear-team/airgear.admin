package com.airgear.admin.controller;


import com.airgear.admin.dto.LoginUserDto;
import com.airgear.admin.model.AuthToken;
import com.airgear.admin.security.TokenProvider;
import com.airgear.admin.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider jwtTokenUtil;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody LoginUserDto userDto) {
        System.out.println("In generate token");

//        if (userService.findByUsername(userDto.getUsername()) == null)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login or password is incorrect!");

        final String token = getToken(userDto);
        return ResponseEntity.ok(new AuthToken(token));
    }

    private String getToken(LoginUserDto user) {
        System.out.println("In get token");
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(authentication);
    }
}
