package com.medicinaviva.authentication.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicinaviva.authentication.model.User;
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
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;

    @Override
    public Patient create(Patient patient) throws BusinessException, ConflictException, UnexpectedException {
        this.isRegistred(patient.getUser());

        boolean enabled = true;
        String password = patient.getUser().getPassword();
        patient.getUser().setPassword(null);
        patient.setActive(enabled);
        patient = this.patientRepository.save(patient);

        User user = patient.getUser();
        user.setPassword(password);
        UserRepresentation userRep = FuncUltils.userRepFactory(user, enabled);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("user_identifier", Collections.singletonList(patient.getId()));
        userRep.setAttributes(attributes);

        this.userService.create(userRep, RolesEnum.PATIENT.getValue());
        return patient;
    }

    private void isRegistred(User user) throws ConflictException {
        Optional<Patient> patientResult = this.patientRepository
                .findByUserUsername(user.getUsername());
        if (patientResult.isPresent())
            throw new ConflictException("Username already taken.");

        patientResult = this.patientRepository
                .findByUserContactEmail(user.getContact().getEmail());
        if (patientResult.isPresent())
            throw new ConflictException("Email already in use.");
    }
}
