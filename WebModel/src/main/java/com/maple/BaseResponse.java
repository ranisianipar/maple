package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class BaseResponse<T> {
    HttpStatus code;
    HttpStatus errorCode; //more specific error repo
    String errorMessage;
    Pageable page;
    T value;
    boolean success;


    public BaseResponse(T value) {
        this.value = value;
    }

    public BaseResponse() {}
}
