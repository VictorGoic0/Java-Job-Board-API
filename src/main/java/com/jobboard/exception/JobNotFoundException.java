package com.jobboard.exception;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(Long id) {
        super("Job not found with id: " + id);
    }
}
