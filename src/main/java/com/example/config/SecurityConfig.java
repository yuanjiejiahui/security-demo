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

    //springboot对security默认配置 ，在工厂中默认创建AuthenticationManager，AuthenticationManager在工厂中暴露

    //    @Autowired
//    public void initialize(AuthenticationManagerBuilder builder) {
////        builder.authenticationProvider() 扩展新的认证方式
//        //builder..
//    }
    //加密方式的注入
//    @Bean
////    public PasswordEncoder passwordEncoder(){
////        return new BCryptPasswordEncoder();
////    }
//自定义AuthenticationManagerBuilder来选择自己的认证方式  推荐，AuthenticationManager并没有在工厂中暴露出来想使用得把她暴露之后再注入
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
        auth.authenticationProvider(usernamePasswordProvider);
        auth.authenticationProvider(mobleAuthencationProvider);
    }

    //把AuthenticationManager暴露到工厂里面，就可以在任何位置注入,如果想暴露一定要把这个方法给注入到容器中
    @Bean
    @Override
    //自定义登录过滤器交给工厂
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //获取工厂对象
//        ApplicationContext applicationContext=http.getSharedObject(ApplicationContext.class);
//        //设置自定义 url 权限管理
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
                .mvcMatchers("/index").permitAll()
                .mvcMatchers("/login.html").permitAll()//放行的资源一定要放在没放行的前面
                .mvcMatchers("/vc.jpg").permitAll()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/hello").authenticated()
                .anyRequest().authenticated() // 除上述放行的url,其余全部鉴权认证
                .and()
                .cors()
                .and()
                .rememberMe()//记住我功能开启
                .rememberMeParameter("remember")//修改请求表单中的的记住我标签name属性
                .rememberMeServices(rememberMeServices())//指定remember的实现方式后端通过json
                .tokenRepository(persistentRememberMeToken())
                //用数据库的形式记住cookie
                .and()
                // 关闭csrf
                .csrf()
                .disable();
                // 基于token,不需要session
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .headers()
//                .cacheControl();// 缓存关闭


        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        Map<String, Object> result = new HashMap<>();
                        System.out.println(authException);
                        result.put("msg", "请先登录");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");//解决中文不显示问题
                        String s = new ObjectMapper().writeValueAsString(result);//转化json
                        response.getWriter().write(String.valueOf(s));
                    }
                });

        http
                .addFilterBefore(jwtAuthenticationTokenFilter,
                        UsernamePasswordAuthenticationFilter.class);

    }

    //数据库实现记住我
    @Bean
    public PersistentTokenRepository persistentRememberMeToken() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }

    //自定义记住我操作主要是前后端分离使用
    @Bean
    public RememberMeServices rememberMeServices() {
        return new RememberConfig(UUID.randomUUID().toString(), userDetailsService(), persistentRememberMeToken());
    }

}
