// sample-application/src/main/java/com/hhgcl/sample_application/user/User.java
package com.hhgcl.sample_application.user;

import java.util.List;

public record User(
    Long id,
    String username,
    String password,
    List<String> roles
) {}