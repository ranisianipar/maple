package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class BaseResponse<T> {
    HttpStatus code;
    HttpStatus errorCode; //more specific error repo
    String errorMessage;
    List value = new ArrayList<T>();
    boolean success;


    public void setValue(T value) {
        this.value.add(value);
    }

    public BaseResponse( List<T> value) {
        this.value = value;
    }

    public BaseResponse(T message) {
        value = new ArrayList<>();
        value.add(message);
    }

    public BaseResponse() {}

    public void errorResponse() {
        this.setCode(HttpStatus.BAD_REQUEST);
        this.setSuccess(false);
    }

    public void succeedResponse() {
        this.setCode(HttpStatus.OK);
        this.setSuccess(true);
    }
}
