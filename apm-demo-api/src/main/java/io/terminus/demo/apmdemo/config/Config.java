package io.terminus.demo.apmdemo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class Config {
    @Value("${terminus.key}")
    private String terminusKey;

    @Value("${terminus.ta.enable}")
    private String taEnable;

    @Value("${terminus.ta.url}")
    private String taUrl;

    @Value("${terminus.ta.collector.url}")
    private String taCollectUrl;
}
