package com.example.demo.controller;

import com.example.demo.forms.ForgotPass;
import com.example.demo.forms.LoginForm;
import com.example.demo.forms.ResetPasswordForm;
import com.example.demo.forms.SignUpForm;
import com.example.demo.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSignup_validInput() throws Exception {
        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setEmail("test@test.com");
        signUpForm.setPassword("test1234");
        signUpForm.setFullName("test user");
        signUpForm.setPhoneNumber("1234567890");
        signUpForm.setAddress("test address");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(signUpForm)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignup_invalidInput() throws Exception {
        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setEmail("");
        signUpForm.setPassword("short");
        signUpForm.setFullName("t");
        signUpForm.setPhoneNumber("1234");
        signUpForm.setAddress("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(signUpForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.fullName").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.address").exists());
    }

    // as our database is in memory, so this test will return 404 as there is no user stored in the database
    // we can make our h2 database persistent in disk and then test this fully
    @Test
    public void testLogin_validInput() throws Exception {
        LoginForm loginForm = new LoginForm();

        loginForm.setEmail("test@test.com");
        loginForm.setPassword("test1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(loginForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testLogin_invalidInput() throws Exception {
        LoginForm loginForm = new LoginForm();

        loginForm.setEmail("@.");
        loginForm.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(loginForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    public void testForgotPassword_validInput() throws Exception {
        ForgotPass forgotPass = new ForgotPass("test@test.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(forgotPass)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testForgotPassword_invalidInput() throws Exception {
        ForgotPass forgotPass = new ForgotPass("@.");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(forgotPass)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void testResetPassword_validInput() throws Exception {
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();

        resetPasswordForm.setEmail("test@test.com");
        resetPasswordForm.setVerificationCode("123456");
        resetPasswordForm.setNewPassword("user123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/reset-password")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(resetPasswordForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testResetPassword_invalidInput() throws Exception {
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();

        resetPasswordForm.setEmail("test@");
        resetPasswordForm.setVerificationCode("1234");
        resetPasswordForm.setNewPassword("user");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/reset-password")
                .contentType("application/json")
                .content(TestUtils.convertObjectToJsonBytes(resetPasswordForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.verificationCode").exists())
                .andExpect(jsonPath("$.newPassword").exists());
    }
}
