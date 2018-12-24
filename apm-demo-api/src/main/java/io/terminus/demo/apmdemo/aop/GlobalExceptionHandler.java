package io.terminus.demo.apmdemo.aop;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleApiConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, Object> result = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            result.put("error", violation.getMessage());
            return result;
        }
        result.put("error", ex.getMessage());
        return result;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleServerException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        Map<String, Object> result = new HashMap<>();
        result.put("error",ex.getMessage());
        return result;
    }
}
