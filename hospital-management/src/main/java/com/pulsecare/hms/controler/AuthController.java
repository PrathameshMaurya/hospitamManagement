package com.pulsecare.hms.controller;

import com.pulsecare.hms.dto.RegisterRequest;
import com.pulsecare.hms.entity.Doctor;
import com.pulsecare.hms.entity.Patient;
import com.pulsecare.hms.entity.Receptionist;
import com.pulsecare.hms.entity.Role;
import com.pulsecare.hms.entity.User;
import com.pulsecare.hms.repository.DoctorRepository;
import com.pulsecare.hms.repository.PatientRepository;
import com.pulsecare.hms.repository.ReceptionistRepository;
import com.pulsecare.hms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ReceptionistRepository receptionistRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest req, RedirectAttributes redirectAttributes) {

        if (req.getPassword() == null || !req.getPassword().equals(req.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Password and Confirm Password do not match.");
            return "redirect:/register";
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "An account with this email already exists.");
            return "redirect:/register";
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.valueOf(req.getRole()));
        user.setCreatedAt(LocalDateTime.now());
        if (req.getDob() != null && !req.getDob().isBlank()) {
            user.setDob(LocalDate.parse(req.getDob()));
        }
        userRepository.save(user);

        switch (user.getRole()) {
            case PATIENT -> {
                Patient patient = new Patient();
                patient.setUser(user);
                patient.setGender(req.getGender());
                patient.setBloodGroup(req.getBloodGroup());
                patient.setEmergencyContact(req.getEmergencyContact());
                patient.setAddress(req.getAddress());
                patientRepository.save(patient);
            }
            case DOCTOR -> {
                Doctor doctor = new Doctor();
                doctor.setUser(user);
                doctor.setSpecialization(req.getSpecialization());
                doctor.setQualification(req.getQualification());
                doctor.setLicenseNumber(req.getLicenseNumber());
                if (req.getExperience() != null && !req.getExperience().isBlank()) {
                    doctor.setExperience(Integer.parseInt(req.getExperience()));
                }
                doctorRepository.save(doctor);
            }
            case RECEPTIONIST -> {
                Receptionist receptionist = new Receptionist();
                receptionist.setUser(user);
                receptionist.setEmployeeId(req.getEmployeeId());
                receptionist.setDepartment(req.getDepartment());
                receptionist.setShift(req.getShift());
                receptionistRepository.save(receptionist);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Account created successfully. Please sign in.");
        return "redirect:/login";
    }
}
