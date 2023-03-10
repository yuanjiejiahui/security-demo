package com.example.config;

import com.example.filter.JwtAuthenticationTokenFilter;
import com.example.filter.MobleAuthencationProvider;
import com.example.filter.UsernamePasswordProvider;
import com.example.filter.impl.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private DataSource dataSource;
    @Resource
    private UserDetailServiceImpl userDetailService;
    @Resource
    private UsernamePasswordProvider usernamePasswordProvider;
    @Resource
    private MobleAuthencationProvider mobleAuthencationProvider;

    //springboot???security???????????? ???????????????????????????AuthenticationManager???AuthenticationManager??????????????????

    //    @Autowired
//    public void initialize(AuthenticationManagerBuilder builder) {
////        builder.authenticationProvider() ????????????????????????
//        //builder..
//    }
    //?????????????????????
//    @Bean
////    public PasswordEncoder passwordEncoder(){
////        return new BCryptPasswordEncoder();
////    }
//?????????AuthenticationManagerBuilder??????????????????????????????  ?????????AuthenticationManager????????????????????????????????????????????????????????????????????????
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
        auth.authenticationProvider(usernamePasswordProvider);
        auth.authenticationProvider(mobleAuthencationProvider);
    }

    //???AuthenticationManager??????????????????????????????????????????????????????,????????????????????????????????????????????????????????????
    @Bean
    @Override
    //????????????????????????????????????
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //??????????????????
//        ApplicationContext applicationContext=http.getSharedObject(ApplicationContext.class);
//        //??????????????? url ????????????
//        http.apply(new UrlAuthorizationConfigurer<>(applicationContext))
//                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
//                            @Override
//                            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
//                                object.setSecurityMetadataSource(customerSecurityMetadataSource);
//                                object.setRejectPublicInvocations(true);
//                                return object;
//                            }
//                        });
        http
                .authorizeRequests()
                .antMatchers("/js/**").permitAll()
                .mvcMatchers("/").permitAll()//????????????????????????????????????????????????
                .mvcMatchers("/code").permitAll()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/hello").authenticated()
                .anyRequest().authenticated() // ??????????????????url,????????????????????????
                .and()
                .cors()
                .and()
                .rememberMe()//?????????????????????
                .rememberMeParameter("remember")//??????????????????????????????????????????name??????
                .rememberMeServices(rememberMeServices())//??????remember???????????????????????????json
                .tokenRepository(persistentRememberMeToken())
                //???????????????????????????cookie
                .and()
                // ??????csrf
                .csrf()
                .disable();
                // ??????token,?????????session
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .headers()
//                .cacheControl();// ????????????


        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        Map<String, Object> result = new HashMap<>();
                        System.out.println(authException);
                        result.put("msg", "????????????");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");//???????????????????????????
                        String s = new ObjectMapper().writeValueAsString(result);//??????json
                        response.getWriter().write(String.valueOf(s));
                    }
                });

        http
                .addFilterBefore(jwtAuthenticationTokenFilter,
                        UsernamePasswordAuthenticationFilter.class);

    }

    //????????????????????????
    @Bean
    public PersistentTokenRepository persistentRememberMeToken() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }

    //??????????????????????????????????????????????????????
    @Bean
    public RememberMeServices rememberMeServices() {
        return new RememberConfig(UUID.randomUUID().toString(), userDetailsService(), persistentRememberMeToken());
    }

}
