package com.medicinaviva.authentication.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private int code;
    private String message;
    private Object body;
}