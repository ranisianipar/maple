package future.maple.web.model;

import java.util.ArrayList;
import java.util.List;

public class BaseResponse<T> {
    int code;
    int errorCode; //more specific error code
    String errorMessage;
    List<T> value;
    boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<T> getValue() {
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



    public BaseResponse(int code, int errorCode, String errorMessage, List<T> value, boolean success) {
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.value = value;
        this.success = success;
    }

    public BaseResponse( List<T> value) {
        this.value = value;
    }

    public BaseResponse(T message) {
        value = new ArrayList<>();
        value.add(message);
    }
}
