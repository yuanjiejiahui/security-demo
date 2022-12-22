package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String,String> result=new HashMap<>();
        result.put("msg","注销成功");
        result.put("status","200");
        response.setContentType("application/json;charset=UTF-8");//解决中文不显示问题
        String s=new ObjectMapper().writeValueAsString(result);//转化json
        response.getWriter().write(String.valueOf(s));
    }
}
