package com.electrocart.user_service.controller;

import com.electrocart.user_service.dto.ChangePasswordRequest;
import com.electrocart.user_service.dto.UpdateUserRequest;
import com.electrocart.user_service.dto.UpdateUserRoleRequest;
import com.electrocart.user_service.dto.UserResponse;
import com.electrocart.user_service.entity.User;
import com.electrocart.user_service.repository.UserRepository;
import com.electrocart.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication){

        System.out.println("Authenticated identity: "
                + authentication.getName());
        return ResponseEntity.ok(
                userService.getCurrentUser(authentication.getName())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
         Authentication authentication,
         @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateCurrentUser(
                        authentication.getName(),
                        request
                )
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request){

        userService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateuserRole(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRoleRequest request){

        UserResponse response = userService.updateUserRole(
                userId,
                request.role()
        );

        return ResponseEntity.ok(response);
    }
}
