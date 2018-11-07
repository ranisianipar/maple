package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class DataConstraintException extends Exception{
    String message;
    HttpStatus code;

    DataConstraintException(String error) {
        this.message = "Data Constraint => "+error;
        this.code = HttpStatus.BAD_REQUEST;
    }

}
