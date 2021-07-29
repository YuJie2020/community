package com.singy.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect // 表示当前类是一个切面类
public class AlphaAspect {

    @Pointcut("execution(* com.singy.community.service.*.*(..))") // 切入点表达式注解
    public void pointcut() {

    }

    @Before("pointcut()") // 前置通知
    public void before() {
        System.out.println("before");
    }

    @AfterReturning("pointcut()") // 后置通知
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()") // 异常通知
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @After("pointcut()") // 最终通知
    public void after() {
        System.out.println("after");
    }

    @Around("pointcut()") // 环绕通知
    public Object around(ProceedingJoinPoint joinPoint) {
        Object returnValue = null;
        try {
            Object[] args = joinPoint.getArgs(); // 得到方法执行所需的参数
            System.out.println("around before");
            returnValue = joinPoint.proceed(args); // 明确调用业务层方法（切入点方法）
            System.out.println("around afterReturning");
            return returnValue;
        } catch (Throwable throwable) {
            System.out.println("around afterThrowing");
            throw new RuntimeException(throwable);
        } finally {
            System.out.println("around after");
        }
    }
}
