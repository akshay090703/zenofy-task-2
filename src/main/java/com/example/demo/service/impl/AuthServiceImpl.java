package com.example.demo.service.impl;

import com.example.demo.enums.CodeType;
import com.example.demo.forms.ForgotPass;
import com.example.demo.forms.LoginForm;
import com.example.demo.forms.ResetPasswordForm;
import com.example.demo.forms.SignUpForm;
import com.example.demo.model.User;
import com.example.demo.model.VerificationCode;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.VerificationCodeRepo;
import com.example.demo.service.AuthService;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.MailSenderUtil;
import com.example.demo.util.ResetCodeUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VerificationCodeRepo verificationCodeRepo;

    @Autowired
    private MailSenderUtil mailSenderUtil;

    @Autowired
    private ResetCodeUtil resetCodeUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> signUp(SignUpForm user) {
        User newUser = User.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();

        Optional<User> oldUser = userRepo.findByEmail(user.getEmail());
        if (oldUser.isPresent()) {
            return new ResponseEntity<>("User with the same email id exists", HttpStatus.CONFLICT);
        }

        User userInDb = userRepo.save(newUser);

        return new ResponseEntity<>(userInDb, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> loginUser(LoginForm user, HttpServletResponse response) {
        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());

        if (existingUser.isEmpty()) {
            return new ResponseEntity<>("User not found" ,HttpStatus.NOT_FOUND);
        }

        if(passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());

            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day

            response.addCookie(cookie);

            return new ResponseEntity<>(existingUser.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Password is incorrect", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPass forgotPass) {
        Optional<User> user = userRepo.findByEmail(forgotPass.getEmail());

        if(user.isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }

        String code = resetCodeUtil.generateResetCode();

        mailSenderUtil.sendVerificationMail(forgotPass.getEmail(), code);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(CodeType.RESET_PASSWORD);
        verificationCode.setUser(user.get());

        verificationCodeRepo.save(verificationCode);

        return new ResponseEntity<>("Code successfully sent to the email", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordForm resetPasswordForm) {
        Optional<User> existingUser = userRepo.findByEmail(resetPasswordForm.getEmail());

        if(existingUser.isEmpty()) {
            return new ResponseEntity<>("User not found" ,HttpStatus.NOT_FOUND);
        }

        Optional<VerificationCode> verificationCode = verificationCodeRepo.findByUser(existingUser.get());

        if(verificationCode.isEmpty()) {
            return new ResponseEntity<>("Verification code not found" ,HttpStatus.NOT_FOUND);
        }

        if(Objects.equals(verificationCode.get().getCode(), resetPasswordForm.getVerificationCode())) {
            existingUser.get().setPassword(passwordEncoder.encode(resetPasswordForm.getNewPassword()));

            userRepo.save(existingUser.get());
            verificationCodeRepo.delete(verificationCode.get());

            return new ResponseEntity<> ("Password successfully changed", HttpStatus.OK);
        }

        return new ResponseEntity<>("Verification code is incorrect" ,HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return new ResponseEntity<>("Logout successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Optional<User> user = userRepo.findByEmail(email);
        if(user.isEmpty()) {
            return new ResponseEntity<>("User not found" ,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
