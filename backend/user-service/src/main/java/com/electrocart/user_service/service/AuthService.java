package com.electrocart.user_service.service;

import com.electrocart.user_service.dto.LoginRequest;
import com.electrocart.user_service.dto.LoginResponse;
import com.electrocart.user_service.dto.RegisterRequest;
import com.electrocart.user_service.dto.UserResponse;
import com.electrocart.user_service.entity.Role;
import com.electrocart.user_service.entity.User;
import com.electrocart.user_service.exception.InvalidCredentialsException;
import com.electrocart.user_service.exception.ResourceAlreadyExistsException;
import com.electrocart.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(()->
                        new InvalidCredentialsException("Invalid email or password!!.."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password!!..");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}
