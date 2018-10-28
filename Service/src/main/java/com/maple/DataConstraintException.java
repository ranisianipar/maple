package com.maple;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DataConstraintException extends Exception{
    String message;
    int code;

    DataConstraintException(String error) {
        this.message = "Data Constraint => "+error;
        this.code = 500;
    }

}
