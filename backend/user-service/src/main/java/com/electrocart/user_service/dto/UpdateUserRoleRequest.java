package com.electrocart.user_service.dto;

import com.electrocart.user_service.entity.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(

        @NotNull(message = "Role is required")
        Role role
) {
}
