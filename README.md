# FitTracker – Backend API

FitTracker is a monolithic backend REST API built using Spring Boot with JWT-based authentication and role-based authorization. The application is containerized with Docker and deployed on Render.

## 🔗 Live Links

- **Live Backend (Render):** [[https://fittracker.onrender.com](https://fitness-mono-z9mw.onrender.com)]([https://fittracker.onrender.com](https://fitness-mono-z9mw.onrender.com))
- **Swagger API Documentation:** [Swagger UI](https://[fittracker.onrender.com/swagger-ui/index.html](http://fitness-mono-z9mw.onrender.com/swagger-ui/index.html))

---

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- Swagger / OpenAPI
- Docker
- Render


---

## Features

- User registration and login functionality  
- JWT-based authentication for secure, stateless sessions  
- Role-Based Access Control (RBAC) with USER and ADMIN roles  
- Protected REST APIs based on user roles  
- Interactive API documentation using Swagger  
- Dockerized application for consistent and portable deployment  

---

## Architecture

- Monolithic architecture  
- RESTful API design  
- Layered structure following industry best practices:
  - Controller layer for handling HTTP requests  
  - Service layer for business logic  
  - Repository layer for data access  
- Stateless authentication using JWT tokens  
- Role-based authorization enforced using Spring Security  

---

## API Documentation

The project includes Swagger (OpenAPI) integration to provide interactive API documentation. Swagger allows developers to explore available endpoints, test API requests, and authenticate using JWT tokens directly from the browser.

---

## Role-Based Access Control (RBAC)

The application enforces role-based authorization to restrict access to endpoints based on user roles.

- USER role has access to standard application APIs  
- ADMIN role has access to privileged and restricted APIs  

User roles are embedded inside the JWT token and validated using Spring Security during request processing.

---

## Authentication Flow

1. A new user registers using the registration API  
2. The user logs in using valid credentials  
3. A JWT token is generated upon successful authentication  
4. The token is included in the Authorization header for subsequent requests  
5. Access to APIs is granted based on the role present in the token  

Authorization Header format:

# FitTracker
