package com.singy.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 引用主配置类（主程序类）
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Redis常见的五大数据类型
     * 		String（字符串）、List（列表）、Set（集合）、Hash（散列）、ZSet（有序集合）
     * 		stringRedisTemplate.opsForValue()   操作String（字符串）
     * 		stringRedisTemplate.opsForList()   操作List（列表）
     * 		stringRedisTemplate.opsForSet()   操作Set（集合）
     * 		stringRedisTemplate.opsForHash()   操作Hash（散列）
     * 		stringRedisTemplate.opsForZSet()   操作ZSet（有序集合）
     */

    @Test
    public void testString() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSet() {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSet() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒")); // 统计排名 reverseRank从大到小倒叙排列
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2)); // 统计范围内的数据 reverseRange从大到小倒叙排列
    }

    @Test
    public void testKey() {
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    /**
     * 多次访问同一个key，使用绑定key的操作对象：
     * BoundValueOperations
     * BoundHashOperations
     * BoundListOperations
     * BoundSetOperations
     * BoundZSetOperations
     */
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        for (int i = 0; i < 5; i++) {
            operations.increment();
        }
        System.out.println(operations.get());
    }

    /**
     * 编程式事务管理
     * Redis的事务管理：启用事务后执行Redis的命令不会立刻执行，
     * 而是将命令存入队列，直到提交事务时将命令一起执行。
     * 问题：在事务未提交前（事务管理过程中）查询不到结果
     * 解决：对于Redis需要将查询操作置于事务管理的过程之外
     */
    @Test
    public void testTransactional() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi(); // 开启事务

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey)); // []（查询结果为空）

                return operations.exec(); // 提交事务
            }
        });
        System.out.println(object); // [1, 1, 1, [zhangsan, wangwu, lisi]]（每一个命令影响的行数/个数及查询结果）
    }
}
