package com.medicinaviva.authentication.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServicesEnum {
    CONSULATION_SERVICE("http://consultation-service");
    private String value;
}
