package com.medicinaviva.authentication.model;

import java.util.Date;

import lombok.Data;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Date birthDate;
    private Contact contact;
}