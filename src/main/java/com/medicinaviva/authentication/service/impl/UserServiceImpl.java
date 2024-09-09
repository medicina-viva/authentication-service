package com.medicinaviva.authentication.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.service.contract.UserService;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;
    private final Tracer tracer;

    @Value("${keycloak.client.realm}")
    private String keycloakClientRealm;

    @Value("${keycloak.client.id}")
    private String keycloakClientId;

    @Value("${keycloak.client.secret}")
    private String keycloakClientSectret;

    @Value("${keycloak.client.url}")
    private String keycloakClientUrl;

    @Override
    public void create(UserRepresentation user, String role) throws BusinessException, ConflictException, UnexpectedException {
        Span createUserInKeycloak = this.tracer.nextSpan().name("createUserInKeycloak");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpan(createUserInKeycloak.start())) {
            RealmResource realmResource = keycloak.realm(this.keycloakClientRealm);
            Response response = realmResource.users().create(user);
            switch (response.getStatus()) {
                case 201 -> {
                    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                    List<RoleRepresentation> roles = Collections.singletonList(
                            realmResource.roles().get(role).toRepresentation()
                    );
                    realmResource.users().get(userId).roles().realmLevel().add(roles);
                    realmResource.users().get(userId).executeActionsEmail(List.of("VERIFY_EMAIL"));
                }
                case 400 -> {
                    String errorMessage = this.readResponseBody(response);
                    throw new BusinessException(errorMessage);
                }
                case 409 -> {
                    String errorMessage = this.readResponseBody(response);
                    throw new ConflictException(errorMessage);
                }
                default -> {
                    String errorMessage = this.readResponseBody(response);
                    throw new UnexpectedException(errorMessage);
                }
            }
        } finally {
            createUserInKeycloak.end();
        }
    }

    @Override
    public String getToken(String username, String password) throws UnauthorizedException, UnexpectedException {
        Span getTokenFromKeycloak = this.tracer.nextSpan().name("getTokenFromKeycloak");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpan(getTokenFromKeycloak.start())) {
            try {
                Keycloak keycloakToken = KeycloakBuilder.builder()
                        .serverUrl(this.keycloakClientUrl)
                        .realm(this.keycloakClientRealm)
                        .grantType(OAuth2Constants.PASSWORD)
                        .clientId(this.keycloakClientId)
                        .clientSecret(this.keycloakClientSectret)
                        .username(username)
                        .password(password)
                        .build();
                AccessTokenResponse response = keycloakToken.tokenManager().getAccessToken();
                return response.getToken();
            } catch (NotAuthorizedException ex) {
                throw new UnauthorizedException("Bad credentials.");
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage());
            }
        } finally {
            getTokenFromKeycloak.end();
        }
    }

    private String readResponseBody(Response response) {
        try (Scanner scanner = new Scanner(response.readEntity(String.class))) {
            String jsonResponse = scanner.useDelimiter("\\A").next();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getString("errorMessage");
        } catch (Exception e) {
            return "Falled to read message error.";
        }
    }

}
