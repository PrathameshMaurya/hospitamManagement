package com.pulsecare.hms.repository;

import com.pulsecare.hms.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    Optional<Receptionist> findByUserEmail(String email);
}
