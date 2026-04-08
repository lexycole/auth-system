Here are all the steps to set up the project using start.spring.io:

Step 1 — Create the Parent Project Scaffold

Go to https://start.spring.io
Set the following:

Project: Maven
Language: Java
Spring Boot: Latest stable (3.x)
Group: com.example
Artifact: auth-system
Packaging: Jar
Java: 25


Do NOT add any dependencies — the parent POM manages versions only
Click Generate → download and unzip the project
This becomes your root auth-system/ folder


Step 2 — Create the core-security-starter Module

Go to https://start.spring.io again (new tab)
Set:

Project: Maven
Group: com.example
Artifact: core-security-starter
Java: 25


Add these dependencies:

🔵 Spring Web
🔵 Spring Security
🔵 OAuth2 Resource Server
🔵 Spring Boot Actuator


Click Generate → download and unzip
Move the unzipped folder into auth-system/ so it sits at auth-system/core-security-starter/


Step 3 — Create the sample-application Module

Go to https://start.spring.io again (new tab)
Set:

Project: Maven
Group: com.example
Artifact: sample-application
Java: 25


Add these dependencies:

🔵 Spring Web
🔵 JDBC API
🔵 Spring Boot DevTools
🔵 Docker Compose Support
🔵 Spring Boot Test (included by default)
🟡 OAuth2 Client (optional — only if adding social login)
🟡 Gateway (optional — only if acting as API gateway)
🟡 Spring Reactive Web (optional — only if using WebClient)


Click Generate → download and unzip
Move the unzipped folder into auth-system/ so it sits at auth-system/sample-application/


Step 4 — Wire the Three Projects Together

Open auth-system/pom.xml (the root one generated in Step 1)
Remove the <dependencies> block Spring Initializr added — the parent manages versions only
Add a <modules> block declaring both core-security-starter and sample-application
Add the spring-cloud-dependencies BOM inside <dependencyManagement> for version management
Open sample-application/pom.xml and add core-security-starter as a local dependency pointing to the sibling module
In both core-security-starter/pom.xml and sample-application/pom.xml, add a <parent> reference pointing up to the root auth-system/pom.xml (replace the spring-boot-starter-parent entry that Initializr generated in each)


Step 5 — Clean Up Initializr Boilerplate

Delete the auto-generated src/ folders from the root auth-system/ project — the root is a parent POM only, it has no source code
Delete the default DemoApplication.java that Initializr created inside core-security-starter/ — it is a library, not a runnable app
Keep the SampleApplication.java that Initializr created inside sample-application/ — rename it if needed to match com.example.app
Delete any auto-generated test stubs inside core-security-starter/test/ — tests live in sample-application/


Step 6 — Add Files Spring Initializr Does NOT Generate

Create docker-compose.yml in the root auth-system/ folder — defines your dev database service
Create Dockerfile inside sample-application/ — two-stage build using eclipse-temurin:25-jdk-alpine and eclipse-temurin:25-jre-alpine
Create schema.sql inside sample-application/src/main/resources/ — the users and roles table definitions
Create the META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports file inside core-security-starter/src/main/resources/ — this is what makes the starter plug-and-play
Add README.md to the root auth-system/ folder


Step 7 — Verify the Structure in Your IDE

Open the root auth-system/ folder in IntelliJ IDEA or VS Code
Confirm Maven recognises it as a multi-module project — you should see all three modules listed in the Maven panel
Run mvn install -DskipTests from the root to confirm the build order resolves correctly: core-security-starter builds first, then sample-application
Confirm no red imports or unresolved dependencies appear in sample-application classes that reference core-security-starter types


Step 8 — Confirm Docker Compose Integration

Make sure Docker Desktop is running
Launch sample-application via mvn spring-boot:run — the spring-boot-docker-compose dependency will auto-start the database defined in docker-compose.yml for you; no manual docker compose up needed in dev mode



<!-- command to run -->
docker build -t sample-application .


What's Next?
  View summary of image vulnerabilities and recommendations → docker scout quickview




Architected JWT-based authentication strategy across modular Spring Boot structure
Architected JWT-based authentication strategy across modular Spring Boot structure
Based on your project structure using spring-boot-starter-security + spring-boot-starter-oauth2-resource-server, here's the full plan:

How it works
core-security-starter holds all reusable security config (JWT validation, filter chain). sample-application holds the login endpoint and user logic.

Files to CREATE
core-security-starter/src/main/java/com/hhgcl/core_security_starter/
File	Purpose
SecurityConfig.java	Filter chain — public vs protected routes, JWT resource server setup
JwtTokenProvider.java	Generate + validate JWT tokens
AuthProperties.java	@ConfigurationProperties for jwt secret, expiry
sample-application/src/main/java/com/hhgcl/sample_application/
File	Purpose
auth/AuthController.java	POST /auth/login — takes credentials, returns JWT
auth/AuthRequest.java	DTO: { username, password }
auth/AuthResponse.java	DTO: { token, expiresIn }
auth/AuthService.java	Validates credentials, calls JwtTokenProvider
user/UserDetailsServiceImpl.java	Loads user from DB for Spring Security
Files to EDIT
core-security-starter
File	What to add
application.properties	app.jwt.secret, app.jwt.expiration-ms
META-INF/spring/org.springframework.boot.autoconfigure...imports	Register SecurityConfig, AuthProperties as auto-configs
sample-application
File	What to add
application.properties	DB credentials, datasource URL, app.jwt.secret
schema.sql	users table DDL (already exists, add columns if needed)
Implementation Steps (in order)
Step 1 — AuthProperties.java (core-security-starter)

java
@ConfigurationProperties(prefix = "app.jwt")
public record AuthProperties(String secret, long expirationMs) {}
Step 2 — JwtTokenProvider.java (core-security-starter)

generateToken(String username) → signs JWT with HMAC secret + expiry
validateToken(String token) → returns claims or throws
getUsernameFromToken(String token)
Step 3 — SecurityConfig.java (core-security-starter)

java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/auth/**").permitAll()
          .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
}
Step 4 — UserDetailsServiceImpl.java (sample-application)

Query users table via JdbcTemplate
Return UserDetails with roles
Step 5 — AuthService.java (sample-application)

Inject AuthenticationManager + JwtTokenProvider
Authenticate credentials → generate token → return AuthResponse
Step 6 — AuthController.java (sample-application)

java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
}
Flow summary
POST /auth/login → AuthController → AuthService
  → AuthenticationManager (validates password)
  → JwtTokenProvider.generateToken()
  → returns { token }

GET /api/protected → SecurityConfig intercepts
  → oauth2ResourceServer validates JWT
  → grants access if valid
Want me to start writing the actual code, beginning with JwtTokenProvider and SecurityConfig?



Complete File Map

core-security-starter/
  └── src/main/java/com/hhgcl/core_security_starter/
        ├── AuthProperties.java         ← NEW
        ├── JwtTokenProvider.java       ← NEW
        ├── JwtAuthenticationFilter.java ← NEW
        ├── SecurityConfig.java         ← NEW
        ├── AuthEntryPoint.java         ← NEW
        ├── AccessDeniedHandlerImpl.java ← NEW
        └── ErrorResponse.java          ← NEW
  └── src/main/resources/META-INF/spring/
        └── ...AutoConfiguration.imports ← EDIT

sample-application/
  └── src/main/java/com/hhgcl/sample_application/
        ├── auth/
        │     ├── AuthController.java   ← NEW
        │     ├── AuthService.java      ← NEW
        │     ├── AuthRequest.java      ← NEW
        │     └── AuthResponse.java     ← NEW
        ├── user/
        │     ├── AppUserDetails.java   ← NEW
        │     └── UserDetailsServiceImpl.java ← NEW
        └── api/
              ├── HealthController.java ← NEW
              ├── UserController.java   ← NEW
              └── AdminController.java  ← NEW
  └── src/main/resources/
        ├── application.properties      ← EDIT
        └── schema.sql                  ← EDIT



docker scout recommendations sample-application:latest

docker build -t sample-application . && docker-compose up -d