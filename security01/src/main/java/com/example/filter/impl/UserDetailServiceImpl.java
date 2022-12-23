package com.example.filter.impl;


import com.example.domain.Role;
import com.example.domain.User;
import com.example.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
@Service
public class UserDetailServiceImpl implements UserDetailsService , UserDetailsPasswordService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private HttpServletRequest request;

    public UserDetails loadUserByUsernamePassword(String username,String password) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsernamePassword(username,password);
      if(!Optional.ofNullable(user).isPresent()){
          throw new UsernameNotFoundException("用户名不存在");
      }

        List<Role> roleList = userMapper.getRolesByUid(user.getId());
        user.setRoles(roleList);
        return user;
    }

    public UserDetails loadUserByPhone(String phone)throws UsernameNotFoundException {
        User user = userMapper.loadUserByPhone(phone);
        if(!Optional.ofNullable(user).isPresent()){
            throw new UsernameNotFoundException("此电话号未注册: "+ phone);
        }
        List<Role> roleList = userMapper.getRolesByUid(user.getId());
        user.setRoles(roleList);
        return user;

    }
   //默认升级的是BC加密算法
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Integer integer = userMapper.updatePassword(user.getUsername(), newPassword);
        if(integer==1){
            ((User) user).setPassword(newPassword);
        }
        return user;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
