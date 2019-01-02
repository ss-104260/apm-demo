package io.terminus.demo.utils;

/**
 * @author: liuhaoyang
 * @create: 2019-01-02 15:12
 **/
public class DateUtils {
    public static long currentTimeNano() {
        Long cutime = System.currentTimeMillis() * 1000000;
        Long nanoTime = System.nanoTime();
        return cutime + (nanoTime - nanoTime / 1000000 * 1000000);
    }
}
