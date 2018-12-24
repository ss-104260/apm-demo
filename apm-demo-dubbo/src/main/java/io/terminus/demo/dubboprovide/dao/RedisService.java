package io.terminus.demo.dubboprovide.dao;

import io.terminus.demo.dubboprovide.config.RedisConfig;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import java.util.HashSet;

@Component
@Data
public class RedisService {

    @Autowired
    RedisConfig config;

    public JedisSentinelPool getPool() {
        if(null == config.getNodes() || "".equals(config.getNodes())) return null;
        HashSet<String> sentinels = new HashSet<>();
        for(String item: config.getNodes().split("\\,")) {
            sentinels.add(item);
        }
        if(sentinels.isEmpty()) return null;
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMaxTotal(1);
        cfg.setMaxWaitMillis(60000);
        JedisSentinelPool pool = new JedisSentinelPool(config.getMaster(), sentinels, cfg,
                60000, config.getPassword(), config.getDatabase());
        return pool;
    }

    public Jedis getJedis() {
        if(null != config.getHost() && !"".equals(config.getHost())) {
            Jedis jedis = new Jedis(config.getHost(), config.getPort(), 60000);
            if(config.getPassword() != null && !"".equals(config.getPassword())) {
                jedis.auth(config.getPassword());
            }
            return jedis;
        } else if ((config.getMaster() != null && !"".equals(config.getMaster())) &&
                (config.getNodes() != null && !"".equals(config.getNodes()))) {
            return getPool().getResource();
        }
        return null;
    }

}
