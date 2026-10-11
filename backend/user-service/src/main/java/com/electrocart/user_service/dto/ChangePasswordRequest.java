package com.electrocart.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New Password is required")
        @Size(min = 8,message = "New password must be at least 8 characters")
        String newPassword
) {}
