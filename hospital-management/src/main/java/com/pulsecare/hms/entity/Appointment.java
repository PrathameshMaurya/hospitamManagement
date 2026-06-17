package com.pulsecare.hms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private String department;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    /** Used by Thymeleaf templates to pick the matching badge CSS class. */
    @Transient
    public String getStatusBadgeClass() {
        return switch (status) {
            case PENDING -> "badge-pending";
            case CONFIRMED -> "badge-confirmed";
            case COMPLETED -> "badge-completed";
            case CANCELLED -> "badge-cancelled";
        };
    }
}
