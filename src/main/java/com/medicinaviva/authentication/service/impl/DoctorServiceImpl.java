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
import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.persistence.entity.Doctor;
import com.medicinaviva.authentication.persistence.repository.DoctorRepository;
import com.medicinaviva.authentication.service.contract.ConsultationClient;
import com.medicinaviva.authentication.service.contract.DoctorService;
import com.medicinaviva.authentication.service.contract.UserService;
import com.medicinaviva.authentication.utils.FuncUltils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final ConsultationClient consultationClient;
    private final UserService userService;

    @Override
    public Doctor create(Doctor doctor)
            throws BusinessException, ConflictException, UnexpectedException, UnauthorizedException {
        List<Long> specialties = FuncUltils.removeDuplication(doctor.getSpecialties());
        doctor.setSpecialties(specialties);
        this.validateDoctor(doctor);

        boolean enabled = true;
        String password = doctor.getUser().getPassword();
        doctor.getUser().setPassword(null);
        doctor.setActive(enabled);
        doctor = this.doctorRepository.save(doctor);

        User user = doctor.getUser();
        user.setPassword(password);
        UserRepresentation userRep = FuncUltils.userRepFactory(user, enabled);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("user_identifier", Collections.singletonList(doctor.getId()));
        userRep.setAttributes(attributes);

        this.userService.create(userRep, RolesEnum.DOCTOR.getValue());
        return doctor;
    }

    private void validateDoctor(Doctor doctor)
            throws BusinessException, UnexpectedException, ConflictException, UnauthorizedException {
        Optional<Doctor> doctorResult = this.doctorRepository
                .findByUserUsername(doctor.getUser().getUsername());
        if (doctorResult.isPresent())
            throw new ConflictException("Username already taken.");

        doctorResult = this.doctorRepository
                .findByUserContactEmail(doctor.getUser().getContact().getEmail());
        if (doctorResult.isPresent())
            throw new ConflictException("Email already in use.");

        if (doctor.getSpecialties().size() > 2)
            throw new BusinessException("Can't have more than two specialties.");

        boolean existsAllSpecialts = this.consultationClient.existsAllSpecialts(doctor.getSpecialties());
        if (!existsAllSpecialts)
            throw new BusinessException(
                    "An error occurred, thats because onde or"
                            + "all specialties are not registered in the system."
                            + "Please verify the specialties and try again.");
    }
}
