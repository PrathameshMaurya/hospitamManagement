package com.pulsecare.hms.dto;

import lombok.Data;

/**
 * Backs the registration form. All fields are kept as String so that
 * empty inputs from hidden role-specific sections never cause a
 * type-conversion error during form binding. Numeric/date fields are
 * parsed manually inside AuthController.
 */
@Data
public class RegisterRequest {

    // Common fields
    private String fullName;
    private String email;
    private String phone;
    private String dob;
    private String password;
    private String confirmPassword;
    private String role;

    // Patient-only fields
    private String gender;
    private String bloodGroup;
    private String emergencyContact;
    private String address;

    // Doctor-only fields
    private String specialization;
    private String qualification;
    private String experience;
    private String licenseNumber;

    // Receptionist-only fields
    private String employeeId;
    private String department;
    private String shift;
}
