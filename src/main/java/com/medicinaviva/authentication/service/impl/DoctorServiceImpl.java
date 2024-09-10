package com.medicinaviva.authentication.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.medicinaviva.authentication.api.dto.Response;
import com.medicinaviva.authentication.model.enums.RolesEnum;
import com.medicinaviva.authentication.model.enums.ServicesEnum;
import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.persistence.entity.Doctor;
import com.medicinaviva.authentication.persistence.repository.DoctorRepository;
import com.medicinaviva.authentication.service.contract.DoctorService;
import com.medicinaviva.authentication.service.contract.UserService;
import com.medicinaviva.authentication.utils.FuncUltils;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    @Override
    public Doctor create(Doctor doctor) throws BusinessException, ConflictException, UnexpectedException {
        boolean enabled = true;
        List<Long> specialties = FuncUltils.removeDuplication(doctor.getSpecialties());
        doctor.setSpecialties(specialties);
        this.validateDoctor(doctor);
        UserRepresentation user = FuncUltils.userRepFactory(doctor.getUser(), enabled);
        this.userService.create(user, RolesEnum.DOCTOR.getValue());
        doctor.setActive(true);
        doctor.getUser().setPassword(null);
        return this.doctorRepository.save(doctor);
    }

    private Response validateDoctor(Doctor doctor) throws BusinessException, UnexpectedException {
        Span validateDoctor = this.tracer.nextSpan().name("validateDoctor");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpan(validateDoctor.start())) {
            if (doctor.getSpecialties().size() > 2)
                throw new BusinessException("Can't have more than two specialties.");

            String url = ServicesEnum.CONSULATION_SERVICE.getValue() + "/specialties/exists/all/" +
                    doctor.getSpecialties()
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
            Response response = this.webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Response.class).block();
            if (response.getCode() > 200)
                throw new UnexpectedException((String) response.getBody());
            if (!((boolean) response.getBody()))
                throw new BusinessException("One of the specialties you have entered, does not exist in the system.");
            return response;
        } finally {
            validateDoctor.end();
        }
    }
}
