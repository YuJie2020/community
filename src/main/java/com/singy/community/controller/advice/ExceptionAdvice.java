package com.singy.community.controller.advice;

import com.singy.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 表示该类是Controller的全局配置类（默认扫描全部的Bean）
 * 配置此注解表示只去扫描带有Controller注解的Bean
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        /**
         * java.lang.Exception extends Throwable
         * StackTraceElement[] getStackTrace() 提供编程访问由 printStackTrace() 输出的堆栈跟踪信息。
         */
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) { // 请求方式为异步（需要返回的是JSON字符串）
            // 设置相应消息的MIME类型：plain代表普通字符串，其可以是json格式，但是需手动转换为json字符串
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
