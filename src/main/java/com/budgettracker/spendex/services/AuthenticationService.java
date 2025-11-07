package com.budgettracker.spendex.services;

import com.budgettracker.spendex.dto.AuthenticationRequest;
import com.budgettracker.spendex.dto.AuthenticationResponse;
import com.budgettracker.spendex.dto.RegisterRequest;
import com.budgettracker.spendex.repos.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.budgettracker.spendex.models.User;
import com.budgettracker.spendex.models.Role;

import java.util.HashMap;

@Service
public class AuthenticationService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthenticationService(UserRepo userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // register
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // login
    public AuthenticationResponse  authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        User user = userRepository.findByEmail(jwtService.getEmailFromToken(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        var jwtToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public Boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

}