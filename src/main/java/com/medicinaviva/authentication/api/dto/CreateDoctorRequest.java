package com.medicinaviva.authentication.api.dto;

import java.util.List;

import com.medicinaviva.authentication.model.Address;
import com.medicinaviva.authentication.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDoctorRequest {
    private User user;
    private Address address;
    private List<Long> specialties;
}
