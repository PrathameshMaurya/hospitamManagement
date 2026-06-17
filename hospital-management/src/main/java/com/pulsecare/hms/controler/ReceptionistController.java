package com.pulsecare.hms.controller;

import com.pulsecare.hms.entity.Receptionist;
import com.pulsecare.hms.repository.AppointmentRepository;
import com.pulsecare.hms.repository.DoctorRepository;
import com.pulsecare.hms.repository.PatientRepository;
import com.pulsecare.hms.repository.ReceptionistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receptionist")
@RequiredArgsConstructor
public class ReceptionistController {

    private final ReceptionistRepository receptionistRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Receptionist receptionist = currentReceptionist(authentication);
        model.addAttribute("receptionist", receptionist);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("patientCount", patientRepository.count());
        model.addAttribute("doctorCount", doctorRepository.count());
        model.addAttribute("appointmentCount", appointmentRepository.count());
        return "receptionist-dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String employeeId,
                                 @RequestParam(required = false) String department,
                                 @RequestParam(required = false) String shift,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        Receptionist receptionist = currentReceptionist(authentication);
        receptionist.getUser().setFullName(fullName);
        receptionist.getUser().setPhone(phone);
        receptionist.setEmployeeId(employeeId);
        receptionist.setDepartment(department);
        receptionist.setShift(shift);

        receptionistRepository.save(receptionist);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/receptionist/dashboard";
    }

    private Receptionist currentReceptionist(Authentication authentication) {
        return receptionistRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Receptionist profile not found"));
    }
}
