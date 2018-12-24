package io.terminus.demo.apmdemo.parser.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormParams {

    private Map<String, ArrayList<String>> values = new HashMap<String,ArrayList<String>>();

    public ArrayList<String> getValues(String key) {
        return this.values.get(key);
    }

    public String getValue(String key) {
        ArrayList<String> vals = this.values.get(key);
        if(vals == null) {
            return null;
        }
        if(vals.size() <= 0) {
            return "";
        }
        return vals.get(0);
    }

    public static class InvalidDataException extends InvalidMessageException {
        public InvalidDataException(String msg, Object data) {
            super(msg, data);
        }
    }

    // parse("key1=val1&key2=val 2&key3=val%203") = {"key1":["val1"],"key2":["val 2"],"key3":["val 3"]}
    public static FormParams parse(String queryString) throws InvalidDataException {
        FormParams params = new FormParams();
        String[] kvs = queryString.split("\\&");
        try {
        for(String item : kvs) {
            String[] kv = item.split("\\=");
            String key = URLDecoder.decode(kv[0],"UTF-8");
            ArrayList<String> vals = params.values.get(key);
            if(!params.values.containsKey(key)) {
                vals = new ArrayList<String>();
                params.values.put(key, vals);
            }
            if(kv.length >= 2) {
                vals.add(URLDecoder.decode(kv[1],"UTF-8"));
            }
        }
        } catch (UnsupportedEncodingException e) {
            throw new InvalidDataException("invalid data in message", queryString);
        }
        return params;
    }
}
