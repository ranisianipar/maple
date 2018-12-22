package com.maple.Exception;

import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends MapleException {

    public MethodNotAllowedException(String method) {
        super("Method not allowed => "+method, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
