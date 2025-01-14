package com.example.demo.controller;

import com.example.demo.forms.ForgotPass;
import com.example.demo.forms.LoginForm;
import com.example.demo.forms.ResetPasswordForm;
import com.example.demo.forms.SignUpForm;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpForm user, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return authService.signUp(user);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signin(@RequestBody @Valid LoginForm user, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return authService.loginUser(user, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPass forgotPass, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return authService.forgotPassword(forgotPass);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return authService.resetPassword(resetPasswordForm);
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> signOut(HttpServletResponse response) {
        return authService.logout(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        return authService.getProfile(request);
    }
}
