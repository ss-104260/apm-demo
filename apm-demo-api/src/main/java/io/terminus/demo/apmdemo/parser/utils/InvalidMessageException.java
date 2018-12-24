package io.terminus.demo.apmdemo.parser.utils;

public class InvalidMessageException extends Exception {
    private Object data;
    public Object getData() { return data; }

    public InvalidMessageException(String msg, Object data) {
        super(msg);
        this.data = data;
    }
}
