package com.example.filter;

import com.example.domain.User;
import com.example.filter.impl.UserDetailServiceImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Collection;
@Component
public class MobleAuthencationProvider implements AuthenticationProvider {
    @Resource
    private UserDetailServiceImpl userDetailService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = (User)authentication.getPrincipal();
        String modelPhone = user.getPhone();
        UserDetails userDetails = userDetailService.loadUserByPhone(modelPhone);
        return  new UserAuthenticationToken(userDetails,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthenticationToken.class);
    }
}
