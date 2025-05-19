package com.codewithme.linksphere.authentication.service;

import com.codewithme.linksphere.authentication.dtos.AuthenticationRequestDto;
import com.codewithme.linksphere.authentication.dtos.AuthenticationResponseDto;
import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import jakarta.validation.Valid;

public interface IAuthenticationService {
    AuthenticationResponseDto registerUser(AuthenticationRequestDto authenticationRequestDto);
    void sendEmailVerificationToken(String email);
    void validateEmailVerificationToken(String token, String email);
    AuthenticationUser getUserByEmail(String email);

    AuthenticationResponseDto loginUser(@Valid AuthenticationRequestDto authenticationRequestDto);

    void sendPasswordResetToken(String email);

    void resetPassword(String email, String newPassword, String token);

    AuthenticationUser updateUserProfile(Long id, String firstName, String lastName, String company, String position, String location);

    void deleteUser(Long id);
}
