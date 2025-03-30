package com.codewithme.linksphere.authentication.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(
        @NotBlank(message = "Email is mandatory") String email,
        @NotBlank(message = "Password is mandatory") String password
) {
}
