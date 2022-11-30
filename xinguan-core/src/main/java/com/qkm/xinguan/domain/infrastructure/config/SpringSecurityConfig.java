package com.qkm.xinguan.domain.infrastructure.config;

import com.qkm.xinguan.domain.infrastructure.filter.JwtAuthorizationFilter;
import com.qkm.xinguan.domain.infrastructure.filter.SecurityLoginFilter;
import com.qkm.xinguan.domain.infrastructure.handler.*;
import com.qkm.xinguan.domain.infrastructure.security.SystemUserDetailsService;
import com.qkm.xinguan.repository.CustomCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/11/07 08:29
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SystemUserDetailsService userDetailsService;

    private final SystemAuthenticationFailureHandler authenticationFailureHandler;

    private final SystemAuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomCacheRepository customCacheRepository;

    private final SystemAuthenticationEntryPoint authenticationEntryPoint;

    private final SystemAccessDeniedHandler accessDeniedHandler;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    public SpringSecurityConfig(SystemUserDetailsService userDetailsService, SystemAuthenticationFailureHandler authenticationFailureHandler, SystemAuthenticationSuccessHandler authenticationSuccessHandler, CustomCacheRepository customCacheRepository, SystemAuthenticationEntryPoint authenticationEntryPoint, SystemAccessDeniedHandler accessDeniedHandler, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.customCacheRepository = customCacheRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    /**
     * 密码加密生成器
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义获取登录体用户名密码的解决方案
     *
     * @return SecurityLoginFilter
     */
    @Bean
    public SecurityLoginFilter securityLoginFilter() throws Exception {
        SecurityLoginFilter securityLoginFilter = new SecurityLoginFilter(customCacheRepository);
        securityLoginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        securityLoginFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        securityLoginFilter.setAuthenticationManager(authenticationManager());
        return securityLoginFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.GET,
                "/favicon.ico",
                "/**/*.png",
                "/**/*.ttf",
                "/*.html",
                "/**/*.css",
                "/**/*.js");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 第一步：解决跨域问题，允许OPTIONS请求，很重要，跨域都会发送该请求
        http.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        // 第二步：让Security永远不会创建HttpSession，它不会使用HttpSession来获取SecurityContext
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().headers().cacheControl();

        // 第八步：关闭 iframe 的限制，才可以嵌套显示 druid，接口文档页面
        http.headers().frameOptions().disable();

        // 第三步：放行公共接口与资源文件
//        http.authorizeRequests()
//            .antMatchers(HttpMethod.GET, "/system/user/avatar").permitAll()
//            .antMatchers(HttpMethod.GET, "/system/user/captchaImage/*").permitAll()
//            .antMatchers(HttpMethod.GET,"/swagger-ui.html").permitAll()
//            .antMatchers(HttpMethod.GET,"/doc.html").permitAll()
//            .antMatchers(HttpMethod.POST,"/druid/**").permitAll() // 放行druid
//            .antMatchers(HttpMethod.GET,"/druid/**").permitAll() // 放行druid
//            .antMatchers(HttpMethod.GET,"/webjars/**").permitAll()
//            .antMatchers(HttpMethod.GET,"/v2/**").permitAll()
//            .antMatchers(HttpMethod.GET,"/swagger-resources/**").permitAll()
//            .anyRequest().authenticated();

        // 第四步：拦截token，并检测。替换 BasicAuthenticationFilter 添加 JwtAuthenticationTokenFilter
        http.addFilterAt(jwtAuthorizationFilter, BasicAuthenticationFilter.class);

        // 第五步：处理异常情况：认证失败和权限不足
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler);

        // 第六步：登录表单
        http.addFilterAt(securityLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.formLogin().loginProcessingUrl("/system/user/login");

        // 第七步：注销登录
        // http.logout().addLogoutHandler(logoutHandler).logoutSuccessHandler(logoutSuccessHandler).permitAll();
    }
}
