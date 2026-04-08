# 🚀 Spring Boot Multi-Module Auth System

## 1. Create Projects (start.spring.io)

Create **3 projects**:

### Parent (no dependencies)
- `auth-system` (Maven, Java 21)

### Library Module
- `core-security-starter`
- Add:
  - Spring Web
  - Spring Security
  - OAuth2 Resource Server
  - JSON WEB TOKEN

### App Module
- `sample-application`
- Add:
  - Spring Web
  - JDBC
  - DevTools
  - Docker Compose
  - postgresql
  - spring-boot-maven-plugin

Move both modules into:
auth-system/
├── core-security-starter/
└── sample-application/


---

## 2. Wire Modules

### Root `pom.xml`
- Packaging: `pom`
- Add:
```xml
<modules>
  <module>core-security-starter</module>
  <module>sample-application</module>
</modules>

Child POMs
Remove spring-boot-starter-parent
Add:
<parent>
  <groupId>com.example</groupId>
  <artifactId>auth-system</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</parent>

Link starter → app
<dependency>
  <groupId>com.example</groupId>
  <artifactId>core-security-starter</artifactId>
</dependency>

3. Clean Up
❌ Delete root src/
❌ Delete DemoApplication in starter
✅ Keep app main class
❌ Remove tests in starter

4. Add Missing Files
Root
docker-compose.yml
Starter
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
App
Dockerfile
schema.sql
application.properties

5. Build & Run
mvn clean install
mvn spring-boot:run -pl sample-application

Docker
docker build -t sample-application .
docker-compose up -d

6. Architecture
core-security-starter → reusable JWT/security
sample-application   → login + business logic

Flow
POST /auth/login → generate JWT
GET /api/** → JWT validated automatically


7. Minimal Components
Starter
SecurityConfig
JwtTokenProvider
AuthProperties
App
AuthController
AuthService
UserDetailsService


## ⚖️ Tradeoffs

### 📝 Notes on design decisions and trade-offs

### ✅ Pros
- Reusable security module
- Clean separation of concerns
- Easy to scale into microservices
- Centralized authentication logic
- Single deployable container (avoids multiple containers per module)

### ❌ Cons
- More initial setup complexity
- Cross-module debugging is harder
- Requires internal version management
- Overkill for small/simple apps


✅ Done
Multi-module setup
JWT auth structure
Docker-ready  