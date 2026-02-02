package com.jobboard.exception;

public class InvalidJobDataException extends RuntimeException {

    public InvalidJobDataException(String message) {
        super(message);
    }
}
