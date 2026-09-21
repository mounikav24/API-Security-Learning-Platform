package com.apisec.apilab.service;

import com.apisec.apilab.dto.AuthResponse;
import com.apisec.apilab.dto.LoginRequest;
import com.apisec.apilab.dto.ProfileResponse;
import com.apisec.apilab.dto.RegisterRequest;
import com.apisec.apilab.entity.Role;
import com.apisec.apilab.entity.User;
import com.apisec.apilab.exception.DuplicateEmailException;
import com.apisec.apilab.exception.InvalidCredentialsException;
import com.apisec.apilab.exception.InvalidRegistrationException;
import com.apisec.apilab.repository.UserRepository;
import com.apisec.apilab.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRegistrationException("Password and confirm password do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    public ProfileResponse toProfile(User user) {
        return new ProfileResponse(user.getName(), user.getEmail(), user.getRole().name(), user.getCreatedAt());
    }
}
