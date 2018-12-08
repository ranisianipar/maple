package com.maple.validation;

import com.maple.Exception.MissingParameterException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class MissingParamHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public MissingParameterException handleMissingParams(MissingServletRequestParameterException ex) {
        return new MissingParameterException(ex.getParameterName());
    }

}
