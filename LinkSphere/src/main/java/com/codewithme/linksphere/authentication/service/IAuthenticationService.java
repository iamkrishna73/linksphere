package com.codewithme.linksphere.authentication.service;

import com.codewithme.linksphere.authentication.dtos.AuthenticationRequestDto;
import com.codewithme.linksphere.authentication.dtos.AuthenticationResponseDto;
import com.codewithme.linksphere.authentication.entities.UserEntity;
import jakarta.validation.Valid;

public interface IAuthenticationService {
    AuthenticationResponseDto registerUser(AuthenticationRequestDto authenticationRequestDto);
    void sendEmailVerificationToken(String email);
    void validateEmailVerificationToken(String token, String email);
    UserEntity getUserByEmail(String email);

    AuthenticationResponseDto loginUser(@Valid AuthenticationRequestDto authenticationRequestDto);

    void sendPasswordResetToken(String email);

    void resetPassword(String email, String newPassword, String token);

    UserEntity updateUserProfile(Long id, String firstName, String lastName, String company, String position, String location);
}
