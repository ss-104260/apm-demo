package io.terminus.demo.model;

import lombok.Data;

@Data
public class MetricMeta {
    private Long id;
    private String name;
    private String tags;
    private String fields;
    private String desc;
    private boolean sourceToEs;
    private boolean agg1m;
}
