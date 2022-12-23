package com.example.controller;

import com.example.config.JwtUtil;
import com.example.config.RedisCache;
import com.example.domain.ResponseResult;
import com.example.domain.User;
import com.example.filter.UserAuthenticationToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class LoginController {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
  private RedisCache redisCache;
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user, HttpServletRequest request) throws JsonProcessingException {
        UserAuthenticationToken userAuthenticationToken
                = new UserAuthenticationToken(user);
        Authentication authenticate = authenticationManager.authenticate(userAuthenticationToken);
        //是否通过
        if(!Optional.ofNullable(authenticate).isPresent()){
            throw new RuntimeException("登录失败");
        }
        System.out.println(authenticate.getPrincipal());
        //强转
        User principal = (User) authenticate.getPrincipal();

        //获取userId
        Integer id = principal.getId();

        //把user的信息存到redis中，userid作为key
        redisCache.setCacheObject("login"+"_"+id,principal);

        //通过后用userid生成一个jwt存入ResponseResult
        String jwt = JwtUtil.createJWT(String.valueOf(id));
        Map<String,Object> map=new HashMap<>();
        map.put("token",jwt);


        return new ResponseResult(200,"登录成功",map);
    }
}
