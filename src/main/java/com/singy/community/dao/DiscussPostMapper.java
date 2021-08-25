package com.singy.community.dao;

import com.singy.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 查询当前页显示的所有帖子（查询无需传入userId，即传入0；当在用户个人主页显示个人帖子时需要传入指定的userId）
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit, @Param("orderMode") int orderMode);

    // 查询帖子数量（查询无需传入userId，即传入0；当在用户个人主页显示个人帖子时需要传入指定的userId）
    // 与SQL中的的字段名相对应，当需要动态拼接SQL条件，例如在<if>中使用，必须指定此注解
    int selectDiscussPostRows(@Param("userId") int userId);

    // 增加帖子（表中的除了id和content字段，其它字段都有默认值，即语句不赋值则默认赋空值）
    int insertDiscussPost(DiscussPost discussPost);

    // 查询帖子
    DiscussPost selectDiscussPostById(int id);

    // 更新帖子评论数量
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    // 更改帖子类型
    int updateType(@Param("id") int id, @Param("type") int type);

    // 更改帖子状态
    int updateStatus(@Param("id") int id, @Param("status") int status);

    // 更改帖子分数
    int updateScore(@Param("id") int id, @Param("score") double score);
}
