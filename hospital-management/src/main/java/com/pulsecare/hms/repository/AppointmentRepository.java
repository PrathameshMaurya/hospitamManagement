package com.pulsecare.hms.repository;

import com.pulsecare.hms.entity.Appointment;
import com.pulsecare.hms.entity.Doctor;
import com.pulsecare.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(Patient patient);

    List<Appointment> findByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(Doctor doctor);
}
