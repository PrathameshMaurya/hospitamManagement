package com.pulsecare.hms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Common account information shared by every role.
 * Each role (Patient / Doctor / Receptionist) has its own table
 * that links back to a single User via a one-to-one relationship.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    /** Convenience helper for templates: age in years, computed from dob. */
    @Transient
    public Integer getAge() {
        if (dob == null) {
            return null;
        }
        return java.time.Period.between(dob, LocalDate.now()).getYears();
    }
}
