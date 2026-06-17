package com.pulsecare.hms.entity;

/**
 * The three roles supported by PulseCare HMS.
 * Stored on the User entity and used by Spring Security as "ROLE_<name>".
 */
public enum Role {
    PATIENT,
    DOCTOR,
    RECEPTIONIST
}
