package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domain.Role;
import com.example.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 高毅
* @description 针对表【user】的数据库操作Mapper
* @createDate 2022-11-05 07:33:22
* @Entity com.example.domain.User
*/
public interface UserMapper  {

    User loadUserByUsernamePassword(String username,String password);

    User loadUserByPhone(String phone);
    //根据用户id查询角色
    List<Role> getRolesByUid(Integer uid);
    //更新密码的操作
    Integer updatePassword(@Param("username") String username, @Param("password") String password);

}




