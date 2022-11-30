package com.qkm.xinguan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @description Swagger2 UI API 接口配置
 * @since 2020/09/24 10:50
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${swagger.enable}")
    private boolean swaggerEnable;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qkm.xinguan.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(buildAuthPar())
                .enable(swaggerEnable);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("新冠物资管理系统 API 文档")
                .description("Swagger UI")
                .termsOfServiceUrl("192.168.43.197")
                .contact(new Contact("邱康明", "https://gitee.com/peppa-qiu", "411882767@qq.com"))
                .version("1.0")
                .build();
    }

    private List<Parameter> buildAuthPar() {
        ParameterBuilder auth = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        auth.name("Authorization").description("访问令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(auth.build());
        return pars;
    }
}
