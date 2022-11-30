package com.qkm.xinguan.domain.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/31 23:09
 * @description 自定义注解，用于标注在 Controller 接口上，异步记录操作日志
 */

@Target(ElementType.METHOD) // 只能标注在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerEndpoint {
    String operation() default "";
    String exceptionMessage() default "系统内部异常";
}
