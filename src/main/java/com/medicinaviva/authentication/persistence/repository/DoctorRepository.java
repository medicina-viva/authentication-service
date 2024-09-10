package com.medicinaviva.authentication.persistence.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medicinaviva.authentication.persistence.entity.Doctor;

public interface DoctorRepository extends MongoRepository<Doctor, UUID>{
}
