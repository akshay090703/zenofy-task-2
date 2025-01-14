package com.example.demo.util;


import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class ResetCodeUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateResetCode() {
        StringBuilder resetCode = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            resetCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return resetCode.toString();
    }
}
