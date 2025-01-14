package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailSenderUtil {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationMail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email verification code");
        message.setText("Your verification code is: " + verificationCode);
        mailSender.send(message);
    }
}
