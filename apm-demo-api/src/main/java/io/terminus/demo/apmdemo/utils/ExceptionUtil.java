package io.terminus.demo.apmdemo.utils;

public class ExceptionUtil {
    public static String getCauseTree(Throwable e) {
        String result = e.toString();
        if(e.getCause() != null) {
            result += "\n" + getCauseTree(e.getCause());
        }
        return result;
    }
}
