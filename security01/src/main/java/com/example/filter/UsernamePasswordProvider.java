package com.example.filter;

import com.example.config.KaptchaNotMatchException;
import com.example.domain.User;
import com.example.filter.impl.UserDetailServiceImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class UsernamePasswordProvider implements AuthenticationProvider {
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserDetailServiceImpl userDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = (User) authentication.getPrincipal();

        if(request.getSession().getAttribute("kaptcha") != null){
            if(user.getKaptcha()!=null || "".equals(user.getKaptcha())){
                if (!(request.getSession().getAttribute("kaptcha").equals(user.getKaptcha()))) {
                    throw new KaptchaNotMatchException("验证码不一致");
                }
            }
        }

        UserDetails userDetails = userDetailService.loadUserByUsernamePassword(user.getUsername(),user.getPassword());
        return new UserAuthenticationToken(userDetails,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthenticationToken.class);
    }
}
