package com.qkm.xinguan.domain.infrastructure.config;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/17 18:02
 * @description Redis 缓存配置
 */

@SuppressWarnings("unused")
public final class CustomCacheConfig {

    /**
     * 默认前缀
     */
    public static final String CACHE_NAME_DEFAULT = "Default:";

    /**
     * 登录验证码 存储前缀
     */
    public static final String LOGIN_VERIFY_CODE = "Login:VerifyCode:";

    /**
     * 验证码默认存储有效期
     */
    public static final long LOGIN_VERIFY_CODE_TIME = 60;

}
