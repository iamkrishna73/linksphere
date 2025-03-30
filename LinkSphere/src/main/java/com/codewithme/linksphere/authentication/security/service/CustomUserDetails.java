package com.codewithme.linksphere.authentication.security.service;

import com.codewithme.linksphere.authentication.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;

    @JsonIgnore
    private String password;

    public CustomUserDetails(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static CustomUserDetails build(UserEntity user) {
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Modify this if roles/authorities are needed
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Username is email in this case
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
