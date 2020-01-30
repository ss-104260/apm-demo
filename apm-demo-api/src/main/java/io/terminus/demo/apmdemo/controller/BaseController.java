package io.terminus.demo.apmdemo.controller;

import io.terminus.demo.apmdemo.config.Config;
import io.terminus.demo.apmdemo.parser.utils.GsonUtils;
import io.terminus.demo.utils.Dic;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/", method = RequestMethod.GET)
public class BaseController {

    @Autowired
    @Getter
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

    protected void setAttributes(Model model) {
        model.addAttribute("config", config);
        model.addAttribute("title","监控测试器");
    }

    protected String view(String name) {
        return name;
    }

    @GetMapping("/health/check")
    @ResponseBody
    public String healthCheck(Model model) {
        return "OK";
    }

}
