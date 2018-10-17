package future.maple.service;

public class SimpleException extends Exception{
    String message;
    int code;

    SimpleException(String msg, int code) {
        this.message = msg;
        this.code = code;
    }
    SimpleException(String msg) {
        super(msg);
    }


}
