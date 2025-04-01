package com.codewithme.linksphere.authentication.controller;

import com.codewithme.linksphere.authentication.dtos.AuthenticationRequestDto;
import com.codewithme.linksphere.authentication.dtos.AuthenticationResponseDto;
import com.codewithme.linksphere.authentication.entities.UserEntity;
import com.codewithme.linksphere.authentication.service.IAuthenticationService;
import com.codewithme.linksphere.dtos.Response;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final IAuthenticationService authenticationService;

    public AuthenticationController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/login")
    public AuthenticationResponseDto loginPage(@Valid @RequestBody AuthenticationRequestDto loginRequestBody) {
        System.out.println();
        return authenticationService.loginUser(loginRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationResponseDto registerUser(@Valid @RequestBody AuthenticationRequestDto authenticationRequestDto) {
        return authenticationService.registerUser(authenticationRequestDto);
    }
    @PutMapping("/send-password-reset-token")
    public Response sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return new Response("Password reset token sent successfully.");
    }

    @PutMapping("/reset-password")
    public Response resetPassword(@RequestParam String newPassword, @RequestParam String token,
                                  @RequestParam String email) {
        authenticationService.resetPassword(email, newPassword, token);
        return new Response("Password reset successfully.");
    }
    @PutMapping("/validate-email-verification-token")
    public Response verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") UserEntity user) {
        authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return new Response("Email verified successfully.");
    }
    @GetMapping("/send-email-verification-token")
    public Response sendEmailVerificationToken(@RequestAttribute("authenticatedUser") UserEntity user) {
        //System.out.println(""user.getEmail());
        log.info("userEmail:  {}", user.getEmail());
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return new Response("Email verification token sent successfully.");
    }

    @GetMapping("/user/me")
    public UserEntity getUser(@RequestAttribute("authenticatedUser") UserEntity user) {
        return user;
    }

}
