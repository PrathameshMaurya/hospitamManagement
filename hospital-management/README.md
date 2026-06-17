# PulseCare HMS — Backend (Spring Boot + MySQL + Thymeleaf)

This is the working backend for the hospital management system. It plugs straight
into the frontend pages built earlier (index, register, login, and the three
dashboards), now wired up as Thymeleaf templates under
`src/main/resources/templates/`.

## 1. Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8 running locally (or update the URL to point to your own server)

## 2. Configure the database

Open `src/main/resources/application.properties` and update the username/password
to match your MySQL setup:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

The database `hospital_db` is created automatically on first run.
`spring.jpa.hibernate.ddl-auto=update` will create all the tables
(`users`, `patients`, `doctors`, `receptionists`, `appointments`, `medical_records`)
for you — no manual SQL needed.

## 3. Run the application

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** in your browser.

## 4. How it works

### Registration & roles
- `/register` lets a user sign up as **Patient**, **Doctor**, or **Receptionist**.
- A row is created in `users` (shared fields: name, email, phone, password, dob, role)
  plus a matching row in `patients` / `doctors` / `receptionists` for the
  role-specific fields.
- Passwords are hashed with BCrypt before being stored.

### Login & redirects
- `/login` is handled by Spring Security's form login (`email` + `password`).
- On success, `SecurityConfig`'s `AuthenticationSuccessHandler` reads the user's
  role and redirects to:
  - Patient → `/patient/dashboard`
  - Doctor → `/doctor/dashboard`
  - Receptionist → `/receptionist/dashboard`
- Each `/patient/**`, `/doctor/**`, `/receptionist/**` path is locked to its own
  role via `hasRole(...)` — a patient can never open the doctor or receptionist
  dashboards (and vice versa).

### Patient dashboard (`/patient/dashboard`)
- **Book Appointment** → `POST /patient/appointments/book` creates a new
  `Appointment` (status `PENDING`) for the chosen doctor/department/date/time.
- **My Appointments** → lists all of the patient's appointments with status badges.
- **My Profile** → `POST /patient/profile/update` updates name, phone, dob,
  gender, blood group, emergency contact and address.

### Doctor dashboard (`/doctor/dashboard`)
- **Appointments** → all appointments booked with this doctor.
- **Medical Records** → `POST /doctor/medical-records/save` adds a
  `MedicalRecord` (diagnosis, prescription, notes) for a selected patient.
- **My Profile** → `POST /doctor/profile/update` updates specialization,
  qualification, experience, license number, etc.

### Receptionist dashboard (`/receptionist/dashboard`)
- Read-only directory of **all patients** and **all doctors** with contact
  details, with a quick client-side search box on each table.
- **My Profile** → `POST /receptionist/profile/update` updates employee ID,
  department and shift.

## 5. Project structure

```
src/main/java/com/pulsecare/hms/
├── HmsApplication.java         # main class
├── entity/                     # User, Patient, Doctor, Receptionist,
│                                # Appointment, MedicalRecord, enums
├── repository/                  # Spring Data JPA repositories
├── security/                    # CustomUserDetailsService + SecurityConfig
├── dto/                          # RegisterRequest (registration form)
└── controller/                  # Home, Auth, Patient, Doctor, Receptionist

src/main/resources/
├── application.properties
└── templates/                   # index, register, login, *-dashboard.html
```

## 6. Try it out

1. Register one account for each role (Patient, Doctor, Receptionist) from `/register`.
2. Log in as the **Patient** → book an appointment with the doctor you registered.
3. Log out, log in as the **Doctor** → see the appointment, add a medical record
   for that patient.
4. Log out, log in as the **Receptionist** → see both the patient and the doctor
   in the directory tables.

## 7. Notes / possible next steps

- No email verification or "forgot password" flow yet.
- Appointment status (`PENDING` → `CONFIRMED`/`COMPLETED`/`CANCELLED`) is not yet
  editable from the UI — could add a small "update status" action on the doctor
  or receptionist dashboard.
- Add validation (e.g. `@NotBlank`, `@Email`) on `RegisterRequest` with
  `spring-boot-starter-validation` for stronger input checks.
