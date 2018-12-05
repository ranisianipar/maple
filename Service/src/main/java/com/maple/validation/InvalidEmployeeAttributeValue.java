package com.maple.validation;

import com.maple.Exception.DataConstraintException;
import com.mongodb.MongoWriteException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


public class InvalidEmployeeAttributeValue{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DataConstraintException validationError(MethodArgumentNotValidException ex) {
        return new DataConstraintException("Username, Email, Password, and Name can't be null");
    }

}
