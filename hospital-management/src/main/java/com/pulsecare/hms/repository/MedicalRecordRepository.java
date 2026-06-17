package com.pulsecare.hms.repository;

import com.pulsecare.hms.entity.Doctor;
import com.pulsecare.hms.entity.MedicalRecord;
import com.pulsecare.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByDoctorOrderByVisitDateDesc(Doctor doctor);

    List<MedicalRecord> findByPatientOrderByVisitDateDesc(Patient patient);
}
