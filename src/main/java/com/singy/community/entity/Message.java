package com.singy.community.entity;

import java.util.Date;

public class Message {

    private int id; // 私信id
    private int fromId; // 发送用户id
    private int toId; // 接收用户id
    /**
     * 会话id（conversationId相同的私信处于同一个会话中，且其由fromId_toId或者toId_fromId组成：id小的在前）
     * 即会话由两个用户唯一确定
     */
    private String conversationId;
    private String content; // 私信内容
    private int status; // 私信状态
    private Date createTime; // 私信创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
