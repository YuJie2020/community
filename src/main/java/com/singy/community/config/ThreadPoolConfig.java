package com.singy.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // 启用定时任务
@EnableAsync // 开启 @Async 注解的使用：使普通方法可以在多线程环境下被异步的调用
public class ThreadPoolConfig {
}
