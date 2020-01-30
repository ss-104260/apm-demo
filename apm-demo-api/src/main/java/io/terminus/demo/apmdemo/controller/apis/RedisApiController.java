package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.apmdemo.dao.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/redis", method = RequestMethod.GET)
public class RedisApiController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/get")
    @ResponseBody
    public Object redisGet(@RequestParam String key) {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            return jedis.get("apm-demo-"+key);
        } finally {
            if(jedis != null) jedis.close();
        }
    }

    @GetMapping("/set")
    @ResponseBody
    public String redisSet(@RequestParam String key, @RequestParam String value) {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            jedis.set("apm-demo-"+key, value);
            return "OK";
        } finally {
            if(jedis != null) jedis.close();
        }
    }

    @GetMapping("/sleep")
    @ResponseBody
    public String redisSleep(@RequestParam(defaultValue = "3") int seconds) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            Class<DebugParams> clazz = DebugParams.class;
            Constructor c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            DebugParams params = (DebugParams)c.newInstance();
            Field f = clazz.getDeclaredField("command");
            f.setAccessible(true);
            f.set(params, new String[]{"sleep", ""+seconds});
            return jedis.debug(params);
        } finally {
            if(jedis != null) jedis.close();
        }
    }

    @GetMapping("/wait")
    @ResponseBody
    public List<String> redisWait(@RequestParam(defaultValue = "not-exist-key") String key, @RequestParam(defaultValue = "3") int seconds) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            return jedis.brpop(seconds, key);
        } finally {
            if(jedis != null) jedis.close();
        }
    }

    @GetMapping("/now")
    @ResponseBody
    public List<String> redisTime() {
        Jedis jedis = null;
        try {
            jedis = redisService.getJedis();
            return jedis.time();
        } finally {
            if(jedis != null) jedis.close();
        }
    }
}
