package com.singy.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.singy.community.service.*.*(..))") // 切入点表达式注解
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[ip]，在[时间]，访问了[com.singy.community.service.*()]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        /**
         * 配置的切面：拦截所有Service中的方法，但是不一定所有的Service都是通过Controller访问的，
         * 就代表不一定都可以获取到request对象。eg：存在事件的消费者类中调用了MessageService
         * 防止空指针异常：需要事先判断ServletRequestAttributes是否为空
         */
        if (attributes == null) {
            logger.info(String.format("用户在[%s]，访问了[%s]", now, target));
            return;
        }
        HttpServletRequest request = attributes.getRequest(); // 获取request对象
        String ip = request.getRemoteHost();
        logger.info(String.format("用户[%s]，在[%s]，访问了[%s]", ip, now, target));
    }
}
