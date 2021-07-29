package com.singy.community.dao;

import com.singy.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

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
}