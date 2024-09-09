package com.medicinaviva.authentication.model;

import lombok.Data;

@Data
public class Address {
    private String country;
    private String state;
    private String neighborhood;
    private String street;
    private String number;
    private String zipCode;
}