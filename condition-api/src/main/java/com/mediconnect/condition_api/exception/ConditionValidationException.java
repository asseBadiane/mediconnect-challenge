package com.mediconnect.condition_api.exception;


class ConditionValidationException extends RuntimeException {
    public ConditionValidationException(String message) {
        super(message);
    }
}