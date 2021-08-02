package com.singy.community.dao;

import com.singy.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * message 表：私信（两个用户之间）以及系统通知（系统后台和用户之间）的复用表
 */
@Mapper
public interface MessageMapper {

    // 私信：

    // 查询当前用户的会话列表，针对每个会话只返回（展示）一条最新的私信，且按照时间排序
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表，且按照时间排序
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量：当前用户的未读私信数量/当前用户某一会话的未读私信数量
    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    // 新增消息（发送私信）
    int insertMessage(Message message);

    // 修改消息的状态
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    // 系统通知：

    // 查询某个用户的某个主题下的最新通知
    Message selectLatestNotice(@Param("userId") int userId, @Param("topic") String topic);

    // 查询某个用户的某个主题所包含的通知数量
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查询某个用户的未读通知的数量：某个用户的所有未读通知数量/某个用户的某个主题下的未读通知数量
    int selectNoticeUnreadCount(@Param("userId") int userId, @Param("topic") String topic);

    // 查询某个用户的某个主题所包含的通知列表
    List<Message> selectNotices(@Param("userId") int userId, @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);
}