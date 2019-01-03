package com.maple;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@Data
public class BaseResponse<T> {
    HttpStatus code;
    HttpStatus errorCode; //more specific error repo
    String errorMessage;
    Pageable paging;
    long totalRecords;
    long totalPages;
    T value;
    boolean success;


    public BaseResponse(T value) {
        this.value = value;
    }

    public BaseResponse() {}
}
