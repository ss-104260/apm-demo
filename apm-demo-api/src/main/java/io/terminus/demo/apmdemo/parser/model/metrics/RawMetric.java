package io.terminus.demo.apmdemo.parser.model.metrics;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RawMetric {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    private Map<String, Object> fields = new HashMap<>();

    @Getter
    private Map<String, String> tags = new HashMap<>();

    public RawMetric copy() {
        RawMetric metric = new RawMetric();
        metric.name = this.name;
        metric.timestamp = this.timestamp;
        metric.fields = this.fields;
        metric.tags = this.tags;
        return metric;
    }
}
