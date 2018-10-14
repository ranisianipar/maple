package future.maple.service;


import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class ResponseWrapper {
    int code;
    String errorMessage;
    int errorCode;
    boolean success;
    Object value;

    public ResponseWrapper(int code, String errorMessage, int errorCode, boolean success, Object value) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.success = success;
        this.value = value;
    }
}
