// sample-application/src/main/java/com/hhgcl/sample_application/auth/AuthResponse.java
package com.hhgcl.sample_application.auth;

import java.util.List;

public record AuthResponse(String token, long expiresIn, Long userId, String username, List<String> roles) {}