package io.terminus.demo.apmdemo.controller.apis;

import io.terminus.demo.utils.Dic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/api/exception", method = RequestMethod.GET)
public class ExceptionApiController {

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
        throw new RuntimeException("this is test exception");
    }

}
