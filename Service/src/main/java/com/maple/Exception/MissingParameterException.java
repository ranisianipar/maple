package com.maple.Exception;

import org.springframework.http.HttpStatus;

public class MissingParameterException extends MapleException {
    private String message;
    private HttpStatus code;

    public MissingParameterException(String param) {
        super("Missing parameter => "+param, HttpStatus.BAD_REQUEST);
    }
}
