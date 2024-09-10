package com.medicinaviva.authentication.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medicinaviva.authentication.persistence.entity.Patient;

public interface PatientRepository extends MongoRepository<Patient, UUID>{
    Optional<Patient> findByUserUsername(String username);
    Optional<Patient> findByUserContactEmail(String username);
}
