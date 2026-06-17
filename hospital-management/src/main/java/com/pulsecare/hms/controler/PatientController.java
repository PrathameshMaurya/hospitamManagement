package com.pulsecare.hms.controller;

import com.pulsecare.hms.entity.Appointment;
import com.pulsecare.hms.entity.AppointmentStatus;
import com.pulsecare.hms.entity.Doctor;
import com.pulsecare.hms.entity.Patient;
import com.pulsecare.hms.repository.AppointmentRepository;
import com.pulsecare.hms.repository.DoctorRepository;
import com.pulsecare.hms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Patient patient = currentPatient(authentication);
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("appointments",
                appointmentRepository.findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(patient));
        return "patient-dashboard";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam String department,
                                   @RequestParam Long doctorId,
                                   @RequestParam String appointmentDate,
                                   @RequestParam String appointmentTime,
                                   @RequestParam(required = false) String reason,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        Patient patient = currentPatient(authentication);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDepartment(department);
        appointment.setAppointmentDate(LocalDate.parse(appointmentDate));
        appointment.setAppointmentTime(LocalTime.parse(appointmentTime));
        appointment.setReason(reason);
        appointment.setStatus(AppointmentStatus.PENDING);

        appointmentRepository.save(appointment);
        redirectAttributes.addFlashAttribute("success", "Appointment requested successfully.");
        return "redirect:/patient/dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String dob,
                                 @RequestParam(required = false) String gender,
                                 @RequestParam(required = false) String bloodGroup,
                                 @RequestParam(required = false) String emergencyContact,
                                 @RequestParam(required = false) String address,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        Patient patient = currentPatient(authentication);
        patient.getUser().setFullName(fullName);
        patient.getUser().setPhone(phone);
        if (dob != null && !dob.isBlank()) {
            patient.getUser().setDob(LocalDate.parse(dob));
        }
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setEmergencyContact(emergencyContact);
        patient.setAddress(address);

        patientRepository.save(patient);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/patient/dashboard";
    }

    private Patient currentPatient(Authentication authentication) {
        return patientRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Patient profile not found"));
    }
}
