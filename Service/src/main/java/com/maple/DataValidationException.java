package com.maple;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DataValidationException extends Exception{
    String message;
    int code;

    DataValidationException(char obj) {
        this.message = "Data Constraint ";
        this.code = 500;
    }

}
