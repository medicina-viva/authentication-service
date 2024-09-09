package com.medicinaviva.authentication.utils;

import java.util.List;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.medicinaviva.authentication.model.User;

public class FuncUltils {

    public static UserRepresentation userRepFactory(User user, boolean isActive) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEnabled(isActive);
        userRep.setUsername(user.getUsername());
        userRep.setEmail(user.getContact().getEmail());
        userRep.setFirstName(user.getFirstName());
        userRep.setLastName(user.getLastName());

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(user.getPassword());
        credentials.setTemporary(false);
        userRep.setCredentials(List.of(credentials));
        return userRep;
    }
}
