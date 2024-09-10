package com.medicinaviva.authentication.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medicinaviva.authentication.persistence.entity.Doctor;

public interface DoctorRepository extends MongoRepository<Doctor, UUID> {
    Optional<Doctor> findByUserUsername(String username);

    Optional<Doctor> findByUserContactEmail(String username);
}
