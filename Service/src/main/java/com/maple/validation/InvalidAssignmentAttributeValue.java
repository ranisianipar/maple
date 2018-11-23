package com.maple.validation;

import com.maple.Exception.DataConstraintException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


public class InvalidAssignmentAttributeValue {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DataConstraintException validationError(MethodArgumentNotValidException ex) {
        return new DataConstraintException("Employee ID, Item SKU, status can't be null");
    }


}
