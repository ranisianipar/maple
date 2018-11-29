package com.maple.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class MapleException extends Exception{
    private String message;
    private HttpStatus code;

    public MapleException (String errorMessage, HttpStatus code) {
        this.message = errorMessage;
        this.code = code;
    }
}
