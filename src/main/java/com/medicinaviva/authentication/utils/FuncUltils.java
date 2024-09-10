package com.medicinaviva.authentication.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static List<Long> removeDuplication(List<Long> longs){
        Set<Long> set = new HashSet<>(longs);
        return new ArrayList<>(set);
    }
}
