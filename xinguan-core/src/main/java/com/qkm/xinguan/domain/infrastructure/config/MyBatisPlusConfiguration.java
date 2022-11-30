package com.qkm.xinguan.domain.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiukangming
 * @version 1.0
 * @description MyBatisPlus Configuration 分页插件
 * @since 2020/09/24 15:40
 */

@Configuration
public class MyBatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pi = new PaginationInnerInterceptor();
        pi.setDbType(DbType.MYSQL);
        mp.addInnerInterceptor(pi);
        return mp;
    }

}
