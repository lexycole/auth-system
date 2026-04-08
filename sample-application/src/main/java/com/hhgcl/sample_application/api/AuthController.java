// sample-application/src/main/java/com/hhgcl/sample_application/api/AuthController.java
package com.hhgcl.sample_application.api;

import com.hhgcl.sample_application.auth.AuthService;
import com.hhgcl.sample_application.auth.AuthRequest;
import com.hhgcl.sample_application.auth.AuthResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}