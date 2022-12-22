package com.example.config;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
//自定义记住我功能的实现类
public class RememberConfig extends PersistentTokenBasedRememberMeServices {
    public RememberConfig(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        String paramValue = (String) request.getAttribute("remember-me");
        if (paramValue == null || !paramValue.equalsIgnoreCase("true") && !paramValue.equalsIgnoreCase("on") && !paramValue.equalsIgnoreCase("yes") && !paramValue.equals("1")) {
            this.logger.debug(LogMessage.format("Did not send remember-me cookie (principal did not set parameter '%s')", parameter));
            return false;
        }
            return true;
    }
}
