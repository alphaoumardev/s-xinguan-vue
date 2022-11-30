package com.qkm.xinguan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author qiukangming
 * @version 1.0
 * @description 项目主启动类
 * @since 2020/09/23 22:36
 */

@SpringBootApplication
@EnableTransactionManagement    // 开启事务
public class ApplicationServer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationServer.class);
    }
}
