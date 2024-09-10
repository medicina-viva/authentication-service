package com.medicinaviva.authentication.service.impl;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.medicinaviva.authentication.model.enums.RolesEnum;
import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.persistence.entity.Patient;
import com.medicinaviva.authentication.persistence.repository.PatientRepository;
import com.medicinaviva.authentication.service.contract.PatientService;
import com.medicinaviva.authentication.service.contract.UserService;
import com.medicinaviva.authentication.utils.FuncUltils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;

    @Override
    public Patient create(Patient patient) throws BusinessException, ConflictException, UnexpectedException {
        boolean enabled = true;
        UserRepresentation user = FuncUltils.userRepFactory(patient.getUser(), enabled);
        this.userService.create(user, RolesEnum.PATIENT.getValue());
        patient.setActive(enabled);
        patient.getUser().setPassword(null);
        return this.patientRepository.save(patient);
    }
}
