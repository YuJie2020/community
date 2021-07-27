package com.singy.community.util;

/**
 * 将一些状态标记存为常量，方便后续配置及更改：降低耦合度，避免直接在代码逻辑中改动
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * Remember Me 状态的登录凭证超时时间
     */
    int REMEMBERME_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
