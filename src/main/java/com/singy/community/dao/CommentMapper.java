package com.singy.community.dao;

import com.singy.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 通过实体查询评论/回复
     * @param entityType 实体类型：帖子(ENTITY_TYPE_POST)/评论(ENTITY_TYPE_COMMENT)
     * @param entityId 实体对应的id（键）
     * @param offset 分页查询开始的索引：(当前的页码 - 1) * 每页显示的条数
     * @param limit 每页查询的条数
     * @return
     */
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId, @Param("offset") int offset, @Param("limit") int limit);

    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

    int insertComment(Comment comment);
}
