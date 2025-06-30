package com.mediconnect.condition_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ConditionNotFoundException extends RuntimeException {
    public ConditionNotFoundException(String message) {
        super(message);
    }
}

