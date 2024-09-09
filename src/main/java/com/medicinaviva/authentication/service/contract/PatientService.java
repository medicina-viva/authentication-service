package com.medicinaviva.authentication.service.contract;

import com.medicinaviva.authentication.model.exception.BusinessException;
import com.medicinaviva.authentication.model.exception.ConflictException;
import com.medicinaviva.authentication.model.exception.UnexpectedException;
import com.medicinaviva.authentication.persistence.entity.Patient;

public interface  PatientService {
    Patient create(Patient patient) throws BusinessException,ConflictException,UnexpectedException;
}
