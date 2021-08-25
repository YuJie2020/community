package com.singy.community.config;

import com.singy.community.quartz.AlphaJob;
import com.singy.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置：第一次读取的时候初始化到数据库中，Quartz将来访问数据库来调度任务
@Configuration
public class QuartzConfig {

    /**
     * FactoryBean可简化Bean的实例化过程
     *      1. 通过FactoryBean封装Bean的实例化过程
     *      2. 将FactoryBean装配到Spring容器中
     *      3. 将FactoryBean注入给其他的Bean
     *      4. 该Bean得到的是FactoryBean所管理的对象实例
     */

    // 配置JobDetail：任务详情
//    @Bean // 程序一启动就会被初始化，因此会执行调度任务。不需要使此调度任务启动，注释即可
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class); // 管理的Job（任务）的类型
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup"); // 设置任务的组名
        factoryBean.setDurability(true); // 声明任务为持久保存
        factoryBean.setRequestsRecovery(true); // 设置任务可恢复
        return factoryBean;
    }

    // 配置Trigger（SimpleTriggerFactoryBean, CronTriggerFactoryBean）：任务触发器
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000); // 任务的执行频率：每3s
        factoryBean.setJobDataMap(new JobDataMap()); // 存储Job的状态
        return factoryBean;
    }

    // 配置PostScoreRefreshJob：刷新帖子分数任务的详情
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class); // 管理的Job（任务）的类型
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup"); // 设置任务的组名
        factoryBean.setDurability(true); // 声明任务为持久保存
        factoryBean.setRequestsRecovery(true); // 设置任务可恢复
        return factoryBean;
    }

    // 配置Trigger：任务触发器
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5); // 任务的执行频率：每5min
        factoryBean.setJobDataMap(new JobDataMap()); // 存储Job的状态
        return factoryBean;
    }
}
