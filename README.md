# CGRS-II: AI-Enabled Smart Citizen Grievance Redressal System

## Overview

CGRS-II is a full-stack system designed to support efficient grievance management for citizens. It enables complaint registration, tracking, resolution, and monitoring through a secure and scalable architecture.

The system integrates:

* Aadhaar-based OTP verification (simulated) for identity validation
* JWT-based authentication for secure session management
* Role-based access control for citizens, authorities, and administrators
* Modular backend services with extensibility for AI integration

The project follows a phased development approach and is currently implemented up to advanced backend features.

---

## Project Structure

```
CGRS-II/
 ├── BE/
 │   └── CGRSBackend/      # Spring Boot backend
 └── FE/                   # React Native mobile application (under development)
```

---

## Technology Stack

### Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA (Hibernate)
* Spring Security
* JWT (JSON Web Token)
* PostgreSQL
* Lombok

### Frontend

* React Native (mobile application)

---

## Implemented Features

### 1. Authentication and Security

#### Email/Password Authentication

* User registration (no token generation)
* Login with JWT token issuance

#### Aadhaar OTP Authentication (Simulated)

* OTP generation for Aadhaar number
* OTP verification with expiry handling
* JWT issued only after successful OTP verification

#### Security

* Stateless authentication using JWT
* Role-based authorization
* Protected APIs with token validation

---

### 2. User Roles

* **Citizen**

  * Submit grievances
  * Track complaint status

* **Authority**

  * View grievances assigned to their department
  * Update status and resolve complaints

* **Admin**

  * Monitor all grievances
  * Manage departments and system-level data

---

### 3. Grievance Management

* Create grievance
* View grievances (role-based filtering)
* Update grievance status
* Delete grievance
* Assign grievances to departments

---

### 4. Department Routing

* Grievances are assigned to specific departments
* Authorities handle grievances based on department mapping

---

### 5. Status Tracking (Timeline)

* Maintain history of grievance status updates
* Track lifecycle of a complaint
* Retrieve complete status timeline

---

### 6. Notification System (Basic)

* Notification service layer implemented
* Triggered on:

  * Grievance creation
  * Status updates
* Currently simulated (console-based)

---

### 7. Escalation System

* Automatic escalation of unresolved grievances
* Based on predefined time thresholds
* Implemented using scheduled background tasks

---

## REST API Endpoints

### Authentication

```
POST /auth/register
POST /auth/login
POST /auth/send-otp
POST /auth/verify-otp
```

---

### Grievance

```
POST   /grievances
GET    /grievances
GET    /grievances/{id}
PUT    /grievances/{id}
DELETE /grievances/{id}
GET    /grievances/{id}/history
```

---

### Department

```
POST /admin/departments
GET  /departments
```

---

## Authentication Flow

### Email/Password Login

1. User registers
2. User logs in using credentials
3. JWT token is generated
4. Token is used for accessing secured APIs

---

### Aadhaar OTP Login

1. User enters Aadhaar number
2. OTP is generated and sent (simulated)
3. User verifies OTP
4. JWT token is issued upon successful verification

---

## Database Configuration

Update `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/cgrs_mobile
spring.datasource.username=cgrs_mobile_user
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Setup Instructions

### 1. Clone Repository

```
git clone https://github.com/YOUR_USERNAME/CGRS-II.git
cd CGRS-II/BE/CGRSBackend
```

---

### 2. Configure Database

* Ensure PostgreSQL is running
* Create database: `cgrs_mobile`
* Update credentials in `application.properties`

---

### 3. Run Backend

```
mvn spring-boot:run
```

Or run the main application class from IntelliJ.

---

### 4. API Testing

Use Postman:

* Authenticate via login or OTP
* Use JWT token in header:

```
Authorization: Bearer <token>
```

---

## Frontend (Mobile Application)

The project includes a React Native-based mobile application in the `FE/` directory.

Current status:

* Project structure initialized
* Backend integration in progress

Planned capabilities:

* User authentication (JWT and OTP)
* Complaint submission with location and media
* Complaint tracking and updates

---

## System Architecture Summary

```
Mobile Application (React Native)
        ↓
Aadhaar OTP Verification (Simulated)
        ↓
JWT Authentication
        ↓
Spring Boot Backend
        ↓
PostgreSQL Database
```

---

## Future Enhancements

* AI-based grievance classification (NLP)
* Image-based issue detection (computer vision)
* Sentiment analysis for prioritization
* Firebase Cloud Messaging integration
* Advanced analytics dashboard

---

## Notes

* Aadhaar integration is simulated for academic purposes
* OTP functionality is implemented without external services
* Backend is modular and designed for scalability

---

## License

This project is developed for academic purposes.
