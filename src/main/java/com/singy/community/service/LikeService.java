package com.singy.community.service;

import com.singy.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞（已点过赞则取消赞）
    public void like(int userId, int entityType, int entityId, int entityUserId) { // entityUserId代表实体的用户id（帖子/评论发表用户的id）
/*
        // 生成此实体类对应的Redis数据库中的key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 判断此实体类对应key的值中是否含有此用户的id（当前用户是否已经点过赞）
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) { // 存在当前用户：已点过赞则取消赞
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
*/
        // Redis事务管理：一个业务中连续执行两次及以上的更新的操作则需要加上事务管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // 对于Redis需要将查询操作置于事务管理的过程之外
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi(); // 开启事务

                if (isMember) { // 存在当前用户：已点过赞则取消赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey); // 数量减一
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey); // 数量加一
                }

                return operations.exec(); // 提交事务
            }
        });
    }

    // 查询实体的点赞数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询用户对实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询用户获得赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey); // 不存在key则返回null
        return count == null ? 0 : count.intValue(); // key为null代表用户还未获得过赞
    }
}
