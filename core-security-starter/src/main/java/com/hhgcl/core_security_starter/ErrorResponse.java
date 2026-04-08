// core-security-starter/src/main/java/com/hhgcl/core_security_starter/ErrorResponse.java
package com.hhgcl.core_security_starter;

public record ErrorResponse(
    int status,
    String error,
    String message,
    long timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, System.currentTimeMillis());
    }
}