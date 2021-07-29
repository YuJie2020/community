package com.singy.community.util;

// 生成操作Redis数据库的key
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 某个实体的赞 key -> value（使用redis中的set集合存储点赞用户的id）
     * like:entity:entityType:entityId -> set(userId)
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞 key -> value（使用redis中的string存储用户的点赞数量）
     * like:user:userId -> string(int)
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
