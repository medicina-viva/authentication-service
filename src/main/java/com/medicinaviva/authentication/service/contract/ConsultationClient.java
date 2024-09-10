package com.medicinaviva.authentication.service.contract;

import java.util.List;

import com.medicinaviva.authentication.model.exception.UnauthorizedException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;

public interface ConsultationClient {
    boolean existsAllSpecialts(List<Long> ids) throws UnauthorizedException,UnexpectedException;
}