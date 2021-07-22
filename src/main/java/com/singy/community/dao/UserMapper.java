package com.singy.community.dao;

import com.singy.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    // @Param是MyBatis所提供的作为Dao层的注解，作用是用于传递参数（用于给参数取别名），从而可以与SQL中的的字段名相对应，一般在 2 <= 参数数 <= 5 时使用
    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateHeader(@Param("id") int id, @Param("headerUrl") String headerUrl);

    int updatePassword(@Param("id") int id, @Param("password") String password);

}
