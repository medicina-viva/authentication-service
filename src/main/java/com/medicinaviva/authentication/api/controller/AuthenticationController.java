package com.medicinaviva.authentication.api.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.authentication.api.dto.LoginRequest;
import com.medicinaviva.authentication.api.dto.Response;
import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.service.contract.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Authentication")
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService service;

    // RESILIENCE 4J
    @Retry(name = "keycloak")
    @TimeLimiter(name = "keycloak")
    @CircuitBreaker(name = "keycloak", fallbackMethod = "loginFallbackMethod")

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns successful message."),
        @ApiResponse(responseCode = "400", description = "Bad request happened."),
        @ApiResponse(responseCode = "401", description = "Unauthorized."),
        @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),})
    public CompletableFuture<ResponseEntity<Response>> login(@RequestBody LoginRequest request)  {
        return CompletableFuture.supplyAsync(() -> {
            Response response;
            String error = CommonsControllerValidators.loginValidator(request);
            if (error != null) {
                response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
            }

            try {
                String accessToken = this.service.getToken(request.getUsername(), request.getPassword());
                response = Response
                        .builder()
                        .code(HttpStatus.CREATED.value())
                        .message("OK")
                        .body(accessToken)
                        .build();
            } catch (UnauthorizedException ex) {
                response = Response.builder().code(HttpStatus.UNAUTHORIZED.value()).message(ex.getMessage()).build();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        });
    }

    public CompletableFuture<ResponseEntity<Response>> loginFallbackMethod(LoginRequest request, RuntimeException ex) {
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
