package com.hhgcl.sample_application.auth;

import com.hhgcl.core_security_starter.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.hhgcl.core_security_starter.AuthProperties;
import com.hhgcl.sample_application.user.AppUserDetails;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthProperties authProperties;

    public AuthService(AuthenticationManager authManager,
                       JwtTokenProvider jwtTokenProvider,
                       AuthProperties authProperties) {
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authProperties = authProperties;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        
        String token = jwtTokenProvider.generateToken(authentication, authProperties);
        
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        
        return new AuthResponse(
            token, 
            authProperties.getExpirationMs(),
            userDetails.getId(),
            userDetails.getUsername(),
            roles
        );
    }
}