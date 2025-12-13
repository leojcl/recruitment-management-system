package com.leojcl.recruitmentsystem.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.RESOURCE_ALREADY_EXISTS, message);
    }
}
