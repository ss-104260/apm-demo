package io.terminus.demo.model;

import lombok.Data;

import java.util.Map;

/**
 * @author: liuhaoyang
 * @create: 2019-01-02 14:30
 **/
@Data
public class MetricEvent {

    private String name;
    private long timestamp;
    private Map<String, Object> fields;
    private Map<String, String> tags;
}
