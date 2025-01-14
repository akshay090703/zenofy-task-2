package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestPath = request.getRequestURI();
        return requestPath.startsWith("/api/auth/sign-in") ||
                requestPath.startsWith("/api/auth/sign-up") ||
                requestPath.startsWith("/api/auth/forgot-password") ||
                requestPath.startsWith("/api/auth/reset-password");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = null;

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("jwtToken")) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        try {
            if(jwtToken == null) {
                throw new ServletException("JWT Token not found");
            }

            String email = jwtUtil.extractEmail(jwtToken);

            if(email == null) {
                throw new IllegalArgumentException("Invalid token payload");
            }

            Optional<User> user = userRepo.findByEmail(email);
            if(user.isEmpty()) {
                throw new ServletException("User not found");
            }

            if(jwtUtil.isTokenExpired(jwtToken)) {
                System.out.println("JWT Token invalid");
                throw new IllegalArgumentException("Invalid token");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("email", email);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized access: " + e.getMessage() + "\"}");
            return;
        }
    }
}
