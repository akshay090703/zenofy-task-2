package com.example.demo.repository;

import com.example.demo.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);
}
