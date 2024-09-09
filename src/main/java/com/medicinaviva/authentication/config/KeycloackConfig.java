package com.medicinaviva.authentication.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloackConfig {
    @Value("${keycloak.client.admin.username}")
    private String adminUsername;

    @Value("${keycloak.client.admin.password}")
    private String adminPassword;

    @Value("${keycloak.client.admin.id}")
    private String adminClienId;

    @Value("${keycloak.client.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.client.url}")
    private String keycloakClientUrl;

    @Bean
    public Keycloak keycloak(){
       return KeycloakBuilder
        .builder()
        .serverUrl(this.keycloakClientUrl) 
        .realm(this.adminRealm)              
        .clientId(this.adminClienId)        
        .username(this.adminUsername)            
        .password(this.adminPassword)            
        .grantType(OAuth2Constants.PASSWORD) 
        .build();
    }
}
