package com.maple;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class InvalidItemAttributeValue {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DataConstraintException validationError(MethodArgumentNotValidException ex) {
        return new DataConstraintException("Name, price, quantity can't be null");
    }
}
