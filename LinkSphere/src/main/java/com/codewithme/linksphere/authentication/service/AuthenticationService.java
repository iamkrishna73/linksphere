package com.codewithme.linksphere.authentication.service;

import com.codewithme.linksphere.authentication.dtos.AuthenticationRequestDto;
import com.codewithme.linksphere.authentication.dtos.AuthenticationResponseDto;
import com.codewithme.linksphere.authentication.entities.UserEntity;
import com.codewithme.linksphere.authentication.repositories.UserRepository;
import com.codewithme.linksphere.authentication.security.jwt.JwtUtils;
import com.codewithme.linksphere.authentication.utils.EmailUtils;
import com.codewithme.linksphere.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {
    Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtils emailUtils;
    private final JwtUtils jwtUtils;
    private final int durationInMinutes = 20;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailUtils emailUtils, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailUtils = emailUtils;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponseDto registerUser(AuthenticationRequestDto authenticationRequestDto) {
        if(userRepository.existsByEmail(authenticationRequestDto.email())) {
            //log.error("Email already exists");
            throw  new ResourceNotFoundException("Email already Exists");
        }
        UserEntity user = new UserEntity(authenticationRequestDto.email(), passwordEncoder.encode(authenticationRequestDto.password()));
        String jwtToken = jwtUtils.generateTokenFromUserEmail(authenticationRequestDto.email());
        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = passwordEncoder.encode(emailVerificationToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

        userRepository.save(user);
        String subject = "Email Verification";
        String body = String.format("""
                        Only one step to take full advantage of LinkedIn.
                        
                        Enter this code to verify your email: %s. The code will expire in %s minutes.""",
                emailVerificationToken, durationInMinutes);
        try {
            emailUtils.sendEmail(authenticationRequestDto.email(), subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
        }
        return new AuthenticationResponseDto(jwtToken, "User Registered successfully");
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    @Override
    public AuthenticationResponseDto loginUser(AuthenticationRequestDto authenticationRequestDto) {
        // Check if user exists before authentication
//        UserEntity user = userRepository.findByEmail(authenticationRequestDto.email())
//                .orElseThrow(() -> {
//                    log.error("User with email {} not found", authenticationRequestDto.email());
//                    return new ResourceNotFoundException("User not found");
//                });
        if(!userRepository.existsByEmail(authenticationRequestDto.email())) {
            log.error("User with email {} not found", authenticationRequestDto.email());
            throw new ResourceNotFoundException("User Not Found");
        }

        System.out.println("hello");
        try {
            // Authenticate the user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequestDto.email(),
                            authenticationRequestDto.password()
                    )
            );

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve authenticated username (email)
            String userEmail = authentication.getName();

            // Generate JWT token
            String jwtToken = jwtUtils.generateTokenFromUserEmail(userEmail);

            log.info("User {} logged in successfully", userEmail);

            // Return authentication response with token
            return new AuthenticationResponseDto(jwtToken, "Login successful");

        } catch (BadCredentialsException e) {
            log.error("Invalid password for user {}", authenticationRequestDto.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password", e);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", authenticationRequestDto.email(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed", e);
        }
    }

    /**
     * Email services
     */
    public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }
    public void sendEmailVerificationToken(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent() && !user.get().getEmailVerified()) {
            String emailVerificationToken = generateEmailVerificationToken();
            String hashedToken = passwordEncoder.encode(emailVerificationToken);
            user.get().setEmailVerificationToken(hashedToken);
            user.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            userRepository.save(user.get());
            String subject = "Email Verification";
            String body = String.format("Only one step to take full advantage of LinkedIn.\n\n"
                            + "Enter this code to verify your email: " + "%s\n\n" + "The code will expire in " + "%s"
                            + " minutes.",
                    emailVerificationToken, durationInMinutes);
            try {
                emailUtils.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
        }

    }

    public void validateEmailVerificationToken(String token, String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        boolean isMatch = passwordEncoder.matches(token, user.get().getEmailVerificationToken());
        log.info("isMatch {}",  isMatch );
        if (user.isPresent() && passwordEncoder.matches(token, user.get().getEmailVerificationToken())
                && !user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setEmailVerified(true);
            user.get().setEmailVerificationToken(null);
            user.get().setEmailVerificationTokenExpiryDate(null);
            userRepository.save(user.get());
        } else if (user.isPresent() && passwordEncoder.matches(token, user.get().getEmailVerificationToken())
                && user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email verification token expired.");
        } else {
            throw new IllegalArgumentException("Email verification token failed.");
        }
    }
    public void sendPasswordResetToken(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = passwordEncoder.encode(passwordResetToken);
            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
            userRepository.save(user.get());
            String subject = "Password Reset";
            String body = String.format("""
                            You requested a password reset.
                            
                            Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                    passwordResetToken, durationInMinutes);
            try {
                emailUtils.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public void resetPassword(String email, String newPassword, String token) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(token, user.get().getPasswordResetToken())
                && !user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.get().setPasswordResetToken(null);
            user.get().setPasswordResetTokenExpiryDate(null);
            user.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user.get());
        } else if (user.isPresent() && passwordEncoder.matches(token, user.get().getPasswordResetToken())
                && user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token expired.");
        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }

    @Override
    public UserEntity updateUserProfile(Long id, String firstName, String lastName, String company, String position, String location) {
        UserEntity user = userRepository.findById(id).orElseThrow(()-> {
            throw new RuntimeException("User not found");
        });
        if(firstName != null) {user.setFirstName(firstName);}
        if(lastName != null){user.setLastName(lastName);}
        if (company != null){user.setCompany(company);}
        if(position != null){user.setPosition(position);}
        if(location != null){user.setLocation(location);}
       return userRepository.save(user);
    }

}
