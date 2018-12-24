package io.terminus.demo.utils;

import java.util.HashMap;

public class Dic extends HashMap<String, Object> {
    public Dic() {}
    public Dic(String key, Object value) {
        this.put(key, value);
    }

    public Dic set(String key,Object value) {
        this.put(key, value);
        return this;
    }
}
