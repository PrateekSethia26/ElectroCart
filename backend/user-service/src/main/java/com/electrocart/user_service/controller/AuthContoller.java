package com.electrocart.user_service.controller;

import com.electrocart.user_service.dto.LoginRequest;
import com.electrocart.user_service.dto.LoginResponse;
import com.electrocart.user_service.dto.RegisterRequest;
import com.electrocart.user_service.dto.UserResponse;
import com.electrocart.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthContoller {

    private final AuthService authService;

    public AuthContoller(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login
            (@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody
                                                 RegisterRequest request){
        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
