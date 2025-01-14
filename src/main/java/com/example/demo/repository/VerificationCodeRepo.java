package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, String> {

    Optional<VerificationCode> findByUser(User user);
}
