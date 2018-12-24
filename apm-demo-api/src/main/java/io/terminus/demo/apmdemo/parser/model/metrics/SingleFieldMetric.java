package io.terminus.demo.apmdemo.parser.model.metrics;

import java.util.Map;

public class SingleFieldMetric {
    private String name;
    private long timestamp;
    private Object value;
    private Map<String, String> tags;

    public SingleFieldMetric setName(String name) {
        this.name = name;
        return this;
    }

    public SingleFieldMetric setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public SingleFieldMetric setValue(Object value) {
        this.value = value;
        return this;
    }

    public SingleFieldMetric setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }
}
