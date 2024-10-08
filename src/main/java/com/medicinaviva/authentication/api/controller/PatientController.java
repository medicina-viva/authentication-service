package com.medicinaviva.authentication.api.controller;

import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.authentication.api.dto.CreatePatientRequest;
import com.medicinaviva.authentication.api.dto.Response;
import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.persistence.entity.Patient;
import com.medicinaviva.authentication.service.contract.PatientService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Patients")
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;
    private final ModelMapper mapper;

    // RESILIENCE 4J
    @Retry(name = "breaker")
    @TimeLimiter(name = "breaker")
    @CircuitBreaker(name = "breaker", fallbackMethod = "createPatientFallbackMethod")

    @PostMapping("/register")
    @Operation(summary = "Register for Patients")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Returns successful message."),
        @ApiResponse(responseCode = "400", description = "Bad request happened."),
        @ApiResponse(responseCode = "409", description = "Conflict."),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),})
    public CompletableFuture<ResponseEntity<Response>> create(@RequestBody CreatePatientRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Response response;
            String error = CommonsControllerValidators
                    .createUserValidator(request.getUser(), request.getAddress());
            if (error != null) {
                response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
            }

            try {
                Patient patient = this.mapper.map(request, Patient.class);
                this.service.create(patient);
                response = Response
                        .builder()
                        .code(HttpStatus.CREATED.value())
                        .message("CREATED")
                        .build();
            } catch (BusinessException ex) {
                response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).build();
            } catch (ConflictException ex) {
                response = Response.builder().code(HttpStatus.CONFLICT.value()).message(ex.getMessage()).build();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }

            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        });
    }

    public CompletableFuture<ResponseEntity<Response>> createPatientFallbackMethod(CreatePatientRequest request, RuntimeException ex) {
        Response response = Response
                .builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Service Unavailable")
                .body("We are sorry! Something went wrong, please try after sometime.")
                .build();

        return CompletableFuture.supplyAsync(()
                -> new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()))
        );
    }
}
