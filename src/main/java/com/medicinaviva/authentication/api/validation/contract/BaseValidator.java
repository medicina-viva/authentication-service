package com.medicinaviva.authentication.api.validation.contract;

public abstract class BaseValidator implements Validator {
    protected String fieldName;
    protected Object fieldValue;

    @Override
    public String validate() {
        return "";
    }
}
