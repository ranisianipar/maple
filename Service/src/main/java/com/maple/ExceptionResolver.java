package com.maple;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


public class ExceptionResolver {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DataConstraintException validationError(MethodArgumentNotValidException ex) {
        return new DataConstraintException("Username, Email, Password, and Fullname can't be null");
    }


}
