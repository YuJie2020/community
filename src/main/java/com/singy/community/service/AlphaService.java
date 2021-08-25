package com.singy.community.service;

import com.singy.community.dao.AlphaDao;
import com.singy.community.dao.DiscussPostMapper;
import com.singy.community.dao.UserMapper;
import com.singy.community.entity.DiscussPost;
import com.singy.community.entity.User;
import com.singy.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype") // 指定 bean 的作用范围：singleton prototype ...
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private AlphaDao alphaDao;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
//        System.out.println("实例化AlphaService");
    }

    @PostConstruct // 指定初始化方法：在构造器之后调用
    public void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy // 指定销毁方法：在销毁对象之前调用
    public void destroy() {
//        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }

    /**
     * 1、Spring事务管理：基于注解/配置文件的声明式事务管理
     * propagation: 属性用于指定事物的传播机制（业务方法A调用业务方法B）
     *      REQUIRED:
     *          默认值，表示一定会有事务：支持当前事务（外部事务），如果不存在则创建事务
     *          （A方法调用B方法，A方法没有被事务管理，则给A添加一个事务；A方法有事务即外部事务，则整个按A事务处理）
 *          REQUIRES_NEW:
     *          创建一个新事务，并且暂停当前事务（外部事务）
     *      NESTED：
     *          如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），即A方法调用B方法，
     *          A及B都被各自的事务所管理，两事务都有效；否则等同于REQUIRED
     * isolation：用于指定事务的隔离级别。默认值为DEFAULT，表示使用数据库的默认隔离级别。
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("Hello");
        discussPost.setContent("新人报到！");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abcde"); // 人为制造错误

        return "ok";
    }

    /**
     * 2、Spring事务管理：编程式事务管理（当业务逻辑复杂，但是仅需要管理中间一小部分的逻辑要求时通常使用此种方式）
     */
    public Object save2() {
        // 设置事务的隔离级别及传播机制
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://images.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("你好");
                discussPost.setContent("我是新人！");
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer.valueOf("abcde"); // 人为制造错误

                return "ok";
            }
        });
    }

    @Async // 使普通方法可以在多线程环境下被异步的调用
    public void execute() {
        logger.debug("execute");
    }

    // 定时任务：不需要主动去调用，自动执行
//    @Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2() {
        logger.debug("execute2");
    }
}
