package com.medicinaviva.authentication.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolesEnum {
    PATIENT("PATIENT"),
    DOCTOR("DOCTOR");
    private String value;
}
