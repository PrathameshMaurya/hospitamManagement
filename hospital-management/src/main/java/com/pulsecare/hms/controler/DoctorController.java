package com.pulsecare.hms.controller;

import com.pulsecare.hms.entity.Doctor;
import com.pulsecare.hms.entity.MedicalRecord;
import com.pulsecare.hms.entity.Patient;
import com.pulsecare.hms.repository.AppointmentRepository;
import com.pulsecare.hms.repository.DoctorRepository;
import com.pulsecare.hms.repository.MedicalRecordRepository;
import com.pulsecare.hms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Doctor doctor = currentDoctor(authentication);
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments",
                appointmentRepository.findByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(doctor));
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("records", medicalRecordRepository.findByDoctorOrderByVisitDateDesc(doctor));
        return "doctor-dashboard";
    }

    @PostMapping("/medical-records/save")
    public String saveRecord(@RequestParam Long patientId,
                              @RequestParam String visitDate,
                              @RequestParam(required = false) String diagnosis,
                              @RequestParam(required = false) String prescription,
                              @RequestParam(required = false) String notes,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        Doctor doctor = currentDoctor(authentication);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        MedicalRecord record = new MedicalRecord();
        record.setDoctor(doctor);
        record.setPatient(patient);
        record.setVisitDate(LocalDate.parse(visitDate));
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setNotes(notes);

        medicalRecordRepository.save(record);
        redirectAttributes.addFlashAttribute("success", "Medical record saved.");
        return "redirect:/doctor/dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String specialization,
                                 @RequestParam(required = false) String qualification,
                                 @RequestParam(required = false) String experience,
                                 @RequestParam(required = false) String licenseNumber,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        Doctor doctor = currentDoctor(authentication);
        doctor.getUser().setFullName(fullName);
        doctor.getUser().setPhone(phone);
        doctor.setSpecialization(specialization);
        doctor.setQualification(qualification);
        doctor.setLicenseNumber(licenseNumber);
        if (experience != null && !experience.isBlank()) {
            doctor.setExperience(Integer.parseInt(experience));
        }

        doctorRepository.save(doctor);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/doctor/dashboard";
    }

    private Doctor currentDoctor(Authentication authentication) {
        return doctorRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
    }
}
