package com.singy.community.dao;

import com.singy.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated // 表示此类或方法...已废弃、暂时可用，但以后此类或方法...都不会再更新，后期可能会删除：不推荐使用
public interface LoginTicketMapper {

    @Insert({
            "INSERT INTO login_ticket(user_id, ticket, status, expired) ",
            "VALUES(#{userId}, #{ticket}, #{status}, #{expired})"
    }) // 可以使用大括号定义多个语句使用拼接的方式（需要注意每个语句后需要有空格以防拼接错误）
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "SELECT id, user_id, ticket, status, expired ",
            "FROM login_ticket WHERE ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "UPDATE login_ticket SET status=#{status} WHERE ticket=#{ticket}"
    })
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
