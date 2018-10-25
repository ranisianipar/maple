package maple.Service;

import org.springframework.stereotype.Service;

@Service
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
