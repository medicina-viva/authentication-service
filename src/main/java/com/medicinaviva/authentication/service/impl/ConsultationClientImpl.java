package com.medicinaviva.authentication.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.medicinaviva.authentication.api.dto.Response;
import com.medicinaviva.authentication.model.enums.ServicesEnum;
import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.service.contract.ConsultationClient;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationClientImpl implements ConsultationClient {
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    @Value("${keycloak.client.secret}")
    private String keycloakClientSectret;

    @Value("${keycloak.client.realm}")
    private String keycloakClientRealm;

    @Value("${keycloak.client.id}")
    private String keycloakClientId;

    @Value("${keycloak.client.url}")
    private String keycloakClientUrl;

    @Override
    public boolean existsAllSpecialts(List<Long> ids) throws UnauthorizedException, UnexpectedException {
        Span existsAllSpecialts = this.tracer.nextSpan().name("existsAllSpecialts");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpan(existsAllSpecialts.start())) {
            try {

                Keycloak keycloakToken = KeycloakBuilder.builder()
                        .realm(this.keycloakClientRealm)
                        .serverUrl(this.keycloakClientUrl)
                        .clientId(this.keycloakClientId)
                        .clientSecret(this.keycloakClientSectret)
                        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                        .build();

                AccessTokenResponse keyClackResponse = keycloakToken.tokenManager().getAccessToken();
                String token = keyClackResponse.getToken();
                String uri = ServicesEnum.CONSULATION_SERVICE.getValue() + "/specialties/exists/all/" +
                        ids.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));

                Response response = this.webClientBuilder.build()
                        .get()
                        .uri(uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Response.class).block();
                return (boolean) response.getBody();
            } catch (WebClientResponseException.Unauthorized ex) {
                throw new UnauthorizedException("Bad credentials.");
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage());
            }
        } finally {
            existsAllSpecialts.end();
        }
    }
}
