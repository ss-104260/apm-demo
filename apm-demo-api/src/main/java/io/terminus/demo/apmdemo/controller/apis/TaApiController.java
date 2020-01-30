package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.apmdemo.config.Config;
import io.terminus.demo.apmdemo.parser.utils.GsonUtils;
import io.terminus.demo.apmdemo.parser.utils.IPDB;
import io.terminus.demo.apmdemo.parser.utils.UserAgent;
import io.terminus.demo.utils.Dic;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import qiniu.ip17mon.LocationInfo;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api", method = RequestMethod.GET)
public class TaApiController {

    @Autowired
    protected Config config;

    @GetMapping("ta")
    @ResponseBody
    public String ta() {
        Dic dic = new Dic().set("enabled", "true".equals(config.getTaEnable()))
                .set("ak",config.getTerminusKey())
                .set("url", config.getTaUrl())
                .set("collectorUrl",config.getTaCollectUrl());
        return "window._taConfig=" + GsonUtils.toJson(dic);
    }

    private String getRealIP(HttpServletRequest req) {
        String ip = req.getHeader("X-Real-IP");
        if(ip != null) ip = ip.trim();
        if(Strings.isEmpty(ip)) {
            ip = req.getHeader("X-Forwarded-For");
            if(!Strings.isEmpty(ip)) {
                ip = ip.split("\\,",1)[0].trim();
            }
            if(Strings.isEmpty(ip)) {
                ip = req.getRemoteHost();
            }
        }
        return ip;
    }

    @GetMapping("ta/info")
    @ResponseBody
    public Dic info(HttpServletRequest req) {
        Dic data = new Dic();
        String ip = getRealIP(req);
        data.set("ip", ip);
        LocationInfo local = IPDB.find(ip);
        data.set("location", new Dic().set("country",local.country).set("state", local.state).set("city", local.city).set("isp", local.isp));
        String ua = req.getHeader("User-Agent");
        data.set("ua", UserAgent.parse(ua));
        return data;
    }
}
