package com.medicinaviva.authentication.api.validation.validators;

import com.medicinaviva.authentication.api.validation.contract.BaseValidator;

public class RequiredFieldValidator extends BaseValidator {
    private final String returnMessage;

    public RequiredFieldValidator(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.returnMessage = this.fieldName + " is required.";
    }

    @Override
    public String validate() {
        return switch (this.fieldValue) {
            case String s -> s.trim().isEmpty() ? this.returnMessage : null;
            case Integer i -> i == 0 ? this.returnMessage : null;
            case null -> this.returnMessage;
            default -> null;
        };
    }
}
