package com.example.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }
}
