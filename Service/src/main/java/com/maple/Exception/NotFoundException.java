package com.maple.Exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends MapleException{
    private String message;
    private HttpStatus code;

    public NotFoundException(String obj) {
        super("Not Found => "+obj, HttpStatus.NOT_FOUND);
    }
}
