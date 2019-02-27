package io.terminus.demo.apmdemo.aop;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author: liuhaoyang
 * @create: 2019-01-21 14:50
 **/
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "ExceptionFilter")
public class ExceptionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Filter doFilter");
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            log.error("doFilter exception.", ex);
            throw ex;
        } finally {
            log.info("Filter doingFilter");
        }
    }
}
