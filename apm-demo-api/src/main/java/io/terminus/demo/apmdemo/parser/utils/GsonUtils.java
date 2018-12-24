package io.terminus.demo.apmdemo.parser.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.Charset;

public class GsonUtils {
    private final static Gson gson = new Gson();
    private final static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(String value, Class<T> type) {
        return gson.fromJson(value, type);
    }

    public static String toJson(Object value) {
        return gson.toJson(value);
    }

    public static byte[] toJSONBytes(Object value) {
        return gson.toJson(value).getBytes(Charset.forName("UTF-8"));
    }

    public static String toPrettyJson(Object value) {
        return prettyGson.toJson(value);
    }
}
