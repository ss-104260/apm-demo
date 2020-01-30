package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.utils.Dic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/exception", method = RequestMethod.GET)
public class ExceptionApiController {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionApiController.class);

    @GetMapping("/NullPointer")
    @ResponseBody
    public void nullPointer() {
        throw new NullPointerException("test null pointer");
    }

    @GetMapping("/DivZero")
    @ResponseBody
    public Dic divZero() {
        int n = 0;
        int m = 100 / n;
        return new Dic("result", m);
    }

    @GetMapping("/RuntimeError")
    @ResponseBody
    public void runtimeError() {
        try {
            throw new RuntimeException("this is test exception");
        } catch (RuntimeException e) {
            logger.error("runtime exception", e);
        }
    }

}
