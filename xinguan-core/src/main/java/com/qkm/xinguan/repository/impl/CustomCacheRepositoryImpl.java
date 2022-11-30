package com.qkm.xinguan.repository.impl;

import com.qkm.xinguan.domain.infrastructure.config.CustomCacheConfig;
import com.qkm.xinguan.repository.CustomCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/17 18:07
 * @description
 */

@Service
public class CustomCacheRepositoryImpl implements CustomCacheRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public CustomCacheRepositoryImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 往缓存中设置登录验证码
     *
     * @param uuid       唯一标识
     * @param verifyCode 验证码
     * @param interval   有效时间
     */
    @Override
    public void setLoginVerifyCode(String uuid, String verifyCode, long interval) {
        String key = CustomCacheConfig.LOGIN_VERIFY_CODE + uuid;
        if (isExitKey(key)) {
            removeByKey(key);
        }
        stringRedisTemplate.opsForValue().set(key, verifyCode, interval, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存中的登录验证码
     *
     * @param historyId 验证码前一个ID
     */
    @Override
    public String getLoginVerifyCode(String historyId) {
        return stringRedisTemplate.opsForValue().get(CustomCacheConfig.LOGIN_VERIFY_CODE + historyId);
    }

    /**
     * 删除缓存中的登录验证码
     *
     * @param historyId 验证码前一个ID
     */
    @Override
    public void deleteLoginVerifyCode(String historyId) {
        String key = CustomCacheConfig.LOGIN_VERIFY_CODE + historyId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 移除指定的 key 缓存
     *
     * @param key key
     */
    @Override
    public void removeByKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 查找是否存在指定的 key 缓存
     *
     * @param key key
     * @return boolean
     */
    @Override
    public boolean isExitKey(String key) {
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        return Objects.equals(aBoolean, true);
    }

    /**
     * 查看键的剩余时间：返回数字如果在 3 秒以下，旧当作无效
     *
     * @param key key
     * @return long
     */
    @Override
    public Long getKeyInterval(String key) {
        return stringRedisTemplate.getExpire(key);
    }
}
