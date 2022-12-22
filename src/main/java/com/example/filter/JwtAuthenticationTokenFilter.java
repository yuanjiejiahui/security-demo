package com.example.filter;

import com.example.config.JwtUtil;
import com.example.config.RedisCache;
import com.example.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  {
        //获取token
        String token = request.getHeader("token");
        if(!Optional.ofNullable(token).isPresent()){
            //放行
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        //解析token
        Claims claims=null;
        String userId=null;
        try {
            claims = JwtUtil.parseJWT(token);
            //获取userId
             userId = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
       String redisKey="login"+"_"+userId;
        User user = redisCache.getCacheObject(redisKey);
        if(!Optional.ofNullable(user).isPresent()){
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder
        //TODO 获取权限信息封装到Authentication中
        UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(user,user.getAuthorities());
        SecurityContextHolder.getContext()
                .setAuthentication(userAuthenticationToken);
        //放行
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}