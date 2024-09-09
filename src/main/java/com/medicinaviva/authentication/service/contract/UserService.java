package com.medicinaviva.authentication.service.contract;

import org.keycloak.representations.idm.UserRepresentation;

import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;


public interface  UserService {
    void create(UserRepresentation  user, String role) throws BusinessException,ConflictException,UnexpectedException;
    String getToken(String username,String password) throws UnauthorizedException, UnexpectedException;
}
