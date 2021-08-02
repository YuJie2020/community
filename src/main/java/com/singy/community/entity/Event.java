package com.singy.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    // 主题，事件的类型（评论、点赞 or 关注）
    private String topic;

    // 事件的触发者
    private int userId;

    // 事件所发生的载体（实体：帖子、评论...）
    private int entityType;
    private int entityId;

    // 事件所发生的载体（实体）的作者，也即系统消息发送的对象
    private int entityUserId;

    // 增强适配性：额外的数据
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    /**
     * 对set方法进行改造：以实现链式调用（event.setXXX().setXXX().setXXX()）
     * @param topic 主题参数
     * @return 当前对象
     */
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 使用传入key及value的形式追加到data字段中
     * @param key key
     * @param value value
     * @return 当前对象
     */
    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
