package com.leojcl.recruitmentsystem.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends BaseException {
    public StorageException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.STORAGE_ERROR, message);
    }
}
