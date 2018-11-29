package com.maple.Exception;

import org.springframework.http.HttpStatus;

public class DataConstraintException extends MapleException{
    private String message;
    private HttpStatus code;

    public DataConstraintException(String error) {
        super("Data constraint => "+error, HttpStatus.BAD_REQUEST);
    }

}
