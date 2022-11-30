package com.qkm.xinguan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/24 22:24
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Configure cross origin requests processing.
     *
     * @param registry 注册器
     * @since 4.2
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
