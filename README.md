# 🚀 CGRS-II (Citizen Grievance Redressal System)

## 📌 Overview

CGRS-II is a **Citizen Grievance Redressal System** designed to allow users to register, track, and manage complaints efficiently.
This project follows a **modular full-stack architecture** with a Spring Boot backend and a mobile frontend (to be integrated in Phase 2).

---

## 🏗️ Project Structure

```
CGRS-II/
 ├── BE/                # Backend (Spring Boot)
 │   └── CGRSBackend/
 └── FE/                # Frontend (React Native - upcoming)
```

---

## ⚙️ Tech Stack

### 🔹 Backend

* Java 17
* Spring Boot
* Spring Data JPA (Hibernate)
* Spring Security + JWT
* PostgreSQL

### 🔹 Frontend (Phase 2)

* React Native

---

## ✅ Phase 1 (Completed)

### 🔐 User Management

* User Registration
* User Login (JWT Authentication)
* Role-based access:

  * Citizen
  * Admin
  * Authority

### 📄 Complaint Module

* Create Complaint
* View All Complaints
* View Complaint by ID
* Update Complaint Status
* Delete Complaint

### 🗄️ Database

* Users
* Complaints
* Departments
* Status Logs

---

## 🌐 REST APIs

### Auth APIs

```
POST /auth/register
POST /auth/login
```

### Complaint APIs

```
POST   /complaints
GET    /complaints
GET    /complaints/{id}
PUT    /complaints/{id}
DELETE /complaints/{id}
```

---

## 🛠️ Setup Instructions

### 1️⃣ Clone Repository

```
git clone https://github.com/YOUR_USERNAME/CGRS-II.git
cd CGRS-II
```

---

### 2️⃣ Backend Setup

Go to backend folder:

```
cd BE/CGRSBackend
```

---

### 3️⃣ Configure Database

Update `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/cgrs_mobile
spring.datasource.username=cgrs_mobile_user
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 4️⃣ Run Backend

Using Maven:

```
mvn spring-boot:run
```

OR run from IntelliJ.

---

### 5️⃣ Test APIs

Use Postman:

* Register user
* Login to get JWT token
* Use token for complaint APIs

---

## 🔐 Authentication

* JWT-based authentication
* Include token in headers:

```
Authorization: Bearer <your_token>
```

---

## 📌 Notes

* PostgreSQL must be running locally
* Ensure correct DB credentials
* Tables are auto-created using JPA

---

## 🚀 Upcoming (Phase 2)

* React Native Mobile App
* API Integration with Backend
* UI for Complaint Management

---

## 👥 Team

* Your Name (Backend Development)
* Team Members

---

## 📄 License

This project is developed for academic purposes.
