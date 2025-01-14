package com.example.demo.service;

import com.example.demo.forms.ForgotPass;
import com.example.demo.forms.LoginForm;
import com.example.demo.forms.ResetPasswordForm;
import com.example.demo.forms.SignUpForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> signUp(SignUpForm user);

    ResponseEntity<?> forgotPassword(@Valid ForgotPass forgotPass);

    ResponseEntity<?> loginUser(@Valid LoginForm user, HttpServletResponse response);

    ResponseEntity<?> resetPassword(@Valid ResetPasswordForm resetPasswordForm);

    ResponseEntity<?> logout(HttpServletResponse response);

    ResponseEntity<?> getProfile(HttpServletRequest request);
}
