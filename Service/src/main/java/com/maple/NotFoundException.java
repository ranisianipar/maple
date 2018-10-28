package com.maple;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotFoundException extends Exception{
    String message;
    int code;

    NotFoundException(char obj) {
        this.message = getObject(obj)+" NOT FOUND";
        this.code = 404;
    }

//    to identify the object
    private String getObject(char o) {
        if (o == 'e') return "Employee";
        else if (o == 'i') return "Item";
        else return "Assignment";
    }

}
