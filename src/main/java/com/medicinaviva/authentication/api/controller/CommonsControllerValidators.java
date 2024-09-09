package com.medicinaviva.authentication.api.controller;

import java.util.ArrayList;
import java.util.List;

import com.medicinaviva.authentication.api.dto.LoginRequest;
import com.medicinaviva.authentication.api.validation.ValidationBuilder;
import com.medicinaviva.authentication.api.validation.ValidationComposite;
import com.medicinaviva.authentication.api.validation.contract.Validator;
import com.medicinaviva.authentication.model.Address;
import com.medicinaviva.authentication.model.User;

public class CommonsControllerValidators {

    public static String createUserValidator(User user, Address address) {
        List<Validator> validators = new ArrayList<>();
        //user 
        validators.addAll(ValidationBuilder.of("Fist Name", user.getFirstName()).required().build());
        validators.addAll(ValidationBuilder.of("Last Name", user.getLastName()).required().build());
        validators.addAll(ValidationBuilder.of("Username", user.getUsername()).required().build());
        validators.addAll(ValidationBuilder.of("Password", user.getPassword()).required().build());
        validators.addAll(ValidationBuilder.of("Birth date", user.getBirthDate()).required().build());
        //contact
        validators.addAll(ValidationBuilder.of("Email", user.getContact().getEmail()).required().build());
        //address
        validators.addAll(ValidationBuilder.of("Country", address.getCountry()).required().build());
        validators.addAll(ValidationBuilder.of("State", address.getState()).required().build());
        validators.addAll(ValidationBuilder.of("Neighborhood", address.getNeighborhood()).required().build());
        validators.addAll(ValidationBuilder.of("Street", address.getStreet()).required().build());
        return new ValidationComposite(validators).validate();
    }

    public static String loginValidator(LoginRequest request) {
        List<Validator> validators = new ArrayList<>();
        validators.addAll(ValidationBuilder.of("Username", request.getUsername()).required().build());
        validators.addAll(ValidationBuilder.of("Passwor", request.getPassword()).required().build());
        return new ValidationComposite(validators).validate();
    }
}
