# ShopCart - Backend Documentation

> Document created: May 1, 2026
> Course: Kiểm Thử Phần Mềm (Software Testing)
> Version: 1.1

---

## Table of Contents

1. [Technology Stack](#1-technology-stack)
2. [Dependencies](#2-dependencies-pomxml)
3. [Architecture](#3-backend-architecture)
4. [Database Configuration](#4-database-configuration)
5. [Testing Setup](#5-testing-setup)
6. [How to Run](#6-how-to-run)
7. [API Endpoints](#7-api-endpoints)
8. [Troubleshooting](#8-troubleshooting)
9. [References](#9-references)

---

## 1. Technology Stack

| Component  | Technology      | Version |
| ---------- | --------------- | ------- |
| Framework  | Spring Boot     | 3.5.0   |
| Language   | Java            | 21      |
| Build Tool | Maven           | -       |
| Database   | PostgreSQL      | -       |
| ORM        | Spring Data JPA | -       |
| Testing    | JUnit 5         | -       |
| Mocking    | Mockito         | -       |

---

## 2. Dependencies (pom.xml)

```xml
<!-- Spring Boot Starters -->
- spring-boot-starter-web      (REST API)
- spring-boot-starter-data-jpa (Database)
- spring-boot-starter-validation
- spring-boot-starter-security

<!-- Database -->
- postgresql                   (Driver)

<!-- Utilities -->
- lombok                        (Code generation)

<!-- Testing -->
- spring-boot-starter-test
- spring-security-test
```

---

## 3. Backend Architecture

### 3.1 Package Structure

```bash
com.shopcart/
├── Main.java                    # Spring Boot Application
├── entity/
│   └── TestEntity.java          # JPA Entity (for testing)
├── dto/
│   ├── TestDTO.java             # Data Transfer Object
│   └── TestRequestDTO.java      # Request DTO with validation
├── repository/
│   └── TestRepository.java      # JPA Repository interface
├── service/
│   ├── TestService.java         # Service Interface
│   └── impl/
│       └── TestServiceImpl.java # Service Implementation
└── controller/
    └── TestController.java      # REST Controller
```

### 3.2 Request Flow

```bash
Client Request
    ↓
@RestController (TestController)
    ↓
@TestService (Interface)
    ↓
@TestServiceImpl (Implementation)
    ↓
@TestRepository (JPA)
    ↓
Database (PostgreSQL)
```

---

## 4. Database Configuration

### 4.1 Vercel Neon PostgreSQL

---

## 5. Testing Setup

| Type             | Framework         | Coverage Target |
| ---------------- | ----------------- | --------------- |
| Unit Test        | JUnit 5 + Mockito | ≥ 85%           |
| Integration Test | Spring MockMvc    | -               |

**Test Location**: `src/test/java/com/shopcart/`

---

## 6. How to Run

```bash
cd backend

# Using Maven
mvn spring-boot:run

# Or build and run
./mvnw clean package
java -jar target/shopcart-backend-1.0.0.jar
```

**Backend URL**: `http://localhost:8080`

---

## 7. API Endpoints

### 7.1 Test Endpoints (for verification)

| Method | Endpoint         | Description            |
| ------ | ---------------- | ---------------------- |
| GET    | `/api/test`      | Get all test records   |
| GET    | `/api/test/{id}` | Get test by ID         |
| POST   | `/api/test`      | Create new test record |
| DELETE | `/api/test/{id}` | Delete test by ID      |

### 7.2 Example Request/Response

#### POST /api/test

```bash
# Request
curl -X POST http://localhost:8080/api/test \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Name"}'

# Response
{
  "id": 1,
  "name": "Test Name"
}
```

---

## 8. Troubleshooting

### 8.1 Port already in use

```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process using port 8080
taskkill -PID 88888 -F
```

---

## 9. References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Vercel Neon Documentation](https://vercel.com/docs/storage/vercel-postgres)

---

**Document Version**: 1.1
**Last Updated**: May 1, 2026
**Author**: ShopCart Project Team
