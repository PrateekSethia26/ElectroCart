package com.electrocart.user_service.service;
import com.electrocart.user_service.dto.ChangePasswordRequest;
import com.electrocart.user_service.dto.UpdateUserRequest;
import com.electrocart.user_service.dto.UserResponse;
import com.electrocart.user_service.entity.Role;
import com.electrocart.user_service.entity.User;
import com.electrocart.user_service.exception.InvalidCredentialsException;
import com.electrocart.user_service.exception.ResourceAlreadyExistsException;
import com.electrocart.user_service.exception.ResourceNotFoundException;
import com.electrocart.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserResponse getCurrentUser(String identity) {

        UUID userId = UUID.fromString(identity);

        User user = userRepository.findById(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

//    For updating user details
    public UserResponse updateCurrentUser(String identity, UpdateUserRequest request) {

        UUID userId = UUID.fromString(identity);

        User user = userRepository.findById(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User not found"));

        // Check wether new mail should not belong to another user
        if(!user.getEmail().equals(request.email())
         && userRepository.existsByEmail(request.email())
        ){
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

//    For changing password
    public void changePassword(String identity, ChangePasswordRequest request){
        UUID userId = UUID.fromString(identity);

        User user = userRepository.findById(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword())){
            throw new InvalidCredentialsException("Current Password is incorrect");
        }

        if(passwordEncoder.matches(
                request.newPassword(), user.getPassword()
        )){
            throw new InvalidCredentialsException("New password must be different from current password");
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );
        userRepository.save(user);
    }

    public UserResponse updateUserRole(String targetUserId, Role newRole){

        UUID userId = UUID.fromString(targetUserId);

        User user = userRepository.findById(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User not found"));

        user.setRole(newRole);

        User updateduser = userRepository.save(user);

        return mapToUserResponse(updateduser);
    }

    private UserResponse mapToUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}