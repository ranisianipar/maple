package com.maple.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class NotFoundException extends Exception{
    String message;
    HttpStatus code;

    public NotFoundException(String obj) {
        this.message = obj+" NOT FOUND";
        this.code = HttpStatus.NOT_FOUND;
    }
}
