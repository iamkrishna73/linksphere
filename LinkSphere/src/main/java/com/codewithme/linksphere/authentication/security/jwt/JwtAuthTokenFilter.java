package com.codewithme.linksphere.authentication.security.jwt;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import com.codewithme.linksphere.authentication.security.service.CustomUserDetailsService;
import com.codewithme.linksphere.authentication.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    Logger log = LoggerFactory.getLogger(JwtAuthTokenFilter.class);
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    public JwtAuthTokenFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = parseJwtToken(request);

            if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {
                String email = jwtUtils.getEmailFromToken(jwtToken);

                if (email != null) {  // Ensure email is valid before proceeding
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT Authentication successful for user: {}", email);
                } else {
                  log.warn("JWT token is valid, but email extraction failed.");
                }

                AuthenticationUser user = authenticationService.getUserByEmail(email);
                log.info("User with this email: {}", user.getEmail());
                request.setAttribute("authenticatedUser", user);
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication", e.getMessage());  // Logs full exception stack trace
            throw new RuntimeException("Authentication failed " + e.getMessage()); // Rethrow the exception after logging
        }
        filterChain.doFilter(request, response);
    }
    private String parseJwtToken(HttpServletRequest request) {
        String jwtToken = jwtUtils.getJwtTokenFromHeader(request);
        log.debug("JwtAuthTokenFilter:parseJwtToken: {}");
        return jwtToken;
    }
}
