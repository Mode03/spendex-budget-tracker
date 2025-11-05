package com.budgettracker.spendex.controllers;

import com.budgettracker.spendex.dto.AuthenticationRequest;
import com.budgettracker.spendex.dto.AuthenticationResponse;
import com.budgettracker.spendex.dto.RegisterRequest;
import com.budgettracker.spendex.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
       try {
           AuthenticationResponse res = authenticationService.register(registerRequest);
           return ResponseEntity.ok(res);
       } catch (Exception e) {
           System.err.println("Exception : " + e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            AuthenticationResponse res = authenticationService.authenticate(authenticationRequest);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println("Exception : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestParam("token") String refreshToken) {
        try {
            AuthenticationResponse res = authenticationService.refreshToken(refreshToken);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println("Exception : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        try {
            Boolean res = authenticationService.validateToken(token);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println("Exception : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
