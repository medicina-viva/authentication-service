package com.medicinaviva.authentication.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.medicinaviva.authentication.model.Address;
import com.medicinaviva.authentication.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("patients")
public class Patient {
    @Id
    private String id;
    private User user;
    private Address address;
    private boolean isActive;
}
