package io.terminus.demo.apmdemo.parser.utils;

public class NumberParser {

    public static double parseDouble(String val, double defVal) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return defVal;
        }
    }

    public static long parseLong(String val, long defVal) {
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            return defVal;
        }
    }

    public static long parseLong(String val, long defVal, int radix) {
        try {
            return Long.parseLong(val, radix);
        } catch (Exception e) {
            return defVal;
        }
    }

    public static int parseInt(String val, int defVal) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return defVal;
        }
    }

}
