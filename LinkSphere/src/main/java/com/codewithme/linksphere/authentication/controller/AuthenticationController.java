package com.codewithme.linksphere.authentication.controller;

import com.codewithme.linksphere.authentication.dtos.AuthenticationRequestDto;
import com.codewithme.linksphere.authentication.dtos.AuthenticationResponseDto;
import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import com.codewithme.linksphere.authentication.repositories.UserRepository;
import com.codewithme.linksphere.authentication.service.IAuthenticationService;
import com.codewithme.linksphere.dtos.Response;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final UserRepository userRepository;
    Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final IAuthenticationService authenticationService;

    public AuthenticationController(IAuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }
    @PostMapping("/login")
    public AuthenticationResponseDto loginPage(@Valid @RequestBody AuthenticationRequestDto loginRequestBody) {
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
    public Response verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return new Response("Email verified successfully.");
    }
    @GetMapping("/send-email-verification-token")
    public Response sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        //System.out.println(""user.getEmail());
        log.info("userEmail:  {}", user.getEmail());
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return new Response("Email verification token sent successfully.");
    }

    @GetMapping("/user/me")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return user;
    }

    @PutMapping("/profile/{id}")
    public AuthenticationUser updateProfile(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable Long id,
                                            @RequestParam(required = false) String firstName,
                                            @RequestParam(required = false) String lastName,
                                            @RequestParam(required = false) String company,
                                            @RequestParam(required = false) String position,
                                            @RequestParam(required = false) String location) {
        if(!user.getId().equals(id)) {
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have the permission to update this profile");
        }
        return authenticationService.updateUserProfile(id, firstName, lastName, company, position, location);

    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        log.info("userId:  {}", user.getId());
               authenticationService.deleteUser(user.getId());
        return ResponseEntity.ok("User deleted successfully.");
    }
}