package com.codewithme.linksphere.authentication.repositories;

import com.codewithme.linksphere.authentication.entities.AuthenticationUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthenticationUser, Long> {
    Optional<AuthenticationUser> findByEmail(@NotBlank @Email String email);
    Boolean existsByEmail(@NotBlank @Email String email);

}
