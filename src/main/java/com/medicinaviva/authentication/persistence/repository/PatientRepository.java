package com.medicinaviva.authentication.persistence.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medicinaviva.authentication.persistence.entity.Patient;

public interface PatientRepository extends MongoRepository<Patient, UUID>{
}
