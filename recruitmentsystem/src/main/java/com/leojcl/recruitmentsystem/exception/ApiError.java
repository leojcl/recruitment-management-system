package com.leojcl.recruitmentsystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private ErrorCode code;
    private String message;
    private String path;
    private List<String> details;

    public ApiError() {
    }

    public ApiError(HttpStatus status, ErrorCode code, String message, String path, List<String> details) {
        this.timestamp = OffsetDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = (details == null || details.isEmpty()) ? null : new ArrayList<>(details);
    }

    public static ApiError of(HttpStatus status, ErrorCode code, String message, String path, List<String> details) {
        return new ApiError(status, code, message, path, details);
    }

}
