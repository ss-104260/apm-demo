package io.terminus.demo.mock;

import lombok.Data;

import java.util.Map;

/**
 * @author: liuhaoyang
 * @create: 2019-01-25 11:03
 **/
@Data
public class LogRequest {

    private String source;

    private String id;

    private String content;

    private long timestamp;

    private String stream;

    private long offset;

    private Map<String, String> tags;
}
