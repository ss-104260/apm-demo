package io.terminus.demo.apmdemo.parser.model.trace;

import io.terminus.demo.apmdemo.parser.model.metrics.RawMetric;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Span {

    private String spanId;

    private String traceId;

    private String parentSpanId;

    private String operationName;

    private long startTime;

    private long endTime;

    private Map<String, String> tags = new HashMap<>();

    private String instanceId;

    public static Span fromMetric(RawMetric metric) {
        Span span = new Span();
        span.setTags(metric.getTags());
        span.setTraceId(metric.getTags().get("trace_id"));
        span.setSpanId(metric.getTags().get("span_id"));
        span.setParentSpanId(metric.getTags().get("parent_span_id"));
        span.setInstanceId(metric.getTags().get("instance_id"));
        span.setOperationName(metric.getTags().get("operation_name"));
        span.setStartTime(Long.valueOf(String.valueOf(metric.getFields().get("start_time"))));
        span.setEndTime(Long.valueOf(String.valueOf(metric.getFields().get("end_time"))));
        return span;
    }
}
