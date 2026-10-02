package com.kirtivispute.physio.appointment;

import org.springframework.http.HttpStatus;

public class PortalException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    public PortalException(HttpStatus status, String code, String message) {
        super(message); this.status = status; this.code = code;
    }
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
}
