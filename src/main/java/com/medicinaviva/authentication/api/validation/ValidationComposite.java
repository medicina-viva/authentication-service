package com.medicinaviva.authentication.api.validation;

import com.medicinaviva.authentication.api.validation.contract.Validator;

import java.util.List;

public class ValidationComposite implements Validator {
    private final List<Validator> validators;

    public ValidationComposite(List<Validator> validators) {
        this.validators = validators;
    }

    @Override
    public String validate() {
        for (Validator validator : validators) {
            String error = validator.validate();
            if (error != null) {
                return error;
            }
        }
        return null;
    }
}
