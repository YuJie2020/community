package com.singy.community.util;

// 生成操作Redis数据库的key
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_LOGINTICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

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

    /**
     * 某个用户关注的实体 key -> value（使用redis中的zset存储用户关注的实体id及关注时间）
     * followee:userId:entityType -> zset(entityId, time)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝 key -> value（使用redis中的zset存储实体粉丝的用户id及关注时间）
     * follower:entityType:entityId -> zset(userId, time)
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码 key -> value（使用redis中的string存储验证码）
     * kaptcha:kaptchaOwnerTicket -> string(kaptchaText)
     *      PS: 获取验证码时这个验证码应该是和某个用户相关的，不同的用户验证码是不一样的，
     *          需要识别出来这个验证码是属于哪个用户，但是用户还并未登录，无法获取用户信息。
     *      解决：用户访问登录页面时，给其（某个用户的客户端浏览器）发送凭证（一串随机的字符串），
     *          存入Cookie中，以这个字符串来临时标识这个用户。
 *          ADD: 每一个验证码对应一个标识用户的临时凭证
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录的凭证 key -> value（使用redis中的string存储用户的登录凭证）
     * ticket:loginTicket对象对应的凭证ticket字符串 -> string(loginTicket对象的JSON字符串)
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_LOGINTICKET + SPLIT + ticket;
    }

    /**
     * 用户 key -> value（使用redis中的string存储用户）
     * ticket:userId -> string(user对象的JSON字符串)
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 某天的独立访客（UV）key -> value（使用redis中的HyperLogLog存储访客IP地址）
     * uv:date -> HyperLogLog(访客IP地址)
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 某几天（区间）的独立访客（UV）key -> value（使用redis中的HyperLogLog存储访客IP地址）
     * uv:startDate:endDate -> HyperLogLog(访客IP地址)
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 某天的活跃用户（DAU）key -> value（使用redis中的string按位存储id为索引index的用户今日是否活跃：0/1）
     * dau:date -> string(id为索引index的用户今日是否活跃：0/1)
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 某几天（区间）的活跃用户（DAU）key -> value（使用redis中的string按位存储id为索引index的用户这段时间是否活跃：0/1，即这段时间有一天活跃就算做这段时间活跃）
     * dau:startDate:endDate -> string(id为索引index的用户这段时间是否活跃：0/1)
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 需要更新分数的帖子 key -> value（使用redis中的set集合存储需要更新分数的帖子id）
     * post:score -> set(discussPostId)
     */
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }
}
