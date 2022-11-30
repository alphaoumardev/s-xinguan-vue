package com.qkm.xinguan.repository;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/01/17 18:09
 * @description 缓存操作
 */
public interface CustomCacheRepository {

    /**
     * 往缓存中设置登录验证码
     *
     * @param uuid       唯一标识
     * @param verifyCode 验证码
     * @param interval   有效时间
     */
    void setLoginVerifyCode(String uuid, String verifyCode, long interval);

    /**
     * 获取缓存中的登录验证码
     *
     * @param historyId 验证码前一个ID
     */
    String getLoginVerifyCode(String historyId);

    /**
     * 删除缓存中的登录验证码
     *
     * @param historyId 验证码前一个ID
     */
    void deleteLoginVerifyCode(String historyId);

    /**
     * 移除指定的 key 缓存
     *
     * @param key key
     */
    void removeByKey(String key);

    /**
     * 查找是否存在指定的 key 缓存
     *
     * @param key key
     * @return boolean
     */
    boolean isExitKey(String key);

    /**
     * 查看键的剩余时间：返回数字如果在 3 秒以下，旧当作无效
     *
     * @param key key
     * @return Long
     */
    Long getKeyInterval(String key);

}
