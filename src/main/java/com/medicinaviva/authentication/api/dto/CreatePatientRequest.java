package com.medicinaviva.authentication.api.dto;

import com.medicinaviva.authentication.model.Address;
import com.medicinaviva.authentication.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientRequest {
   private User user;
   private Address address;
}
