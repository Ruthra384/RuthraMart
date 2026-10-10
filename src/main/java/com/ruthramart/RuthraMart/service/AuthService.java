package com.ruthramart.RuthraMart.service;

import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.UserRepository;
import com.ruthramart.RuthraMart.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    // Map of token -> userId
    private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateTokenForUser(User user) {
        String token = "RTM_" + UUID.randomUUID().toString().replace("-", "");
        activeSessions.put(token, user.getId());
        return token;
    }

    public void invalidateToken(String token) {
        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }
            activeSessions.remove(token);
        }
    }

    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(password.trim(), user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> getAuthenticatedUser(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        Long userId = activeSessions.get(token);
        if (userId != null) {
            return userRepository.findById(userId);
        }
        return Optional.empty();
    }

    public Optional<User> getUserFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank()) {
            return getAuthenticatedUser(authHeader);
        }
        String customHeader = request.getHeader("X-Auth-Token");
        if (customHeader != null && !customHeader.isBlank()) {
            return getAuthenticatedUser(customHeader);
        }
        // Also check request parameter or cookie if needed
        String paramToken = request.getParameter("token");
        if (paramToken != null && !paramToken.isBlank()) {
            return getAuthenticatedUser(paramToken);
        }
        return Optional.empty();
    }
}
