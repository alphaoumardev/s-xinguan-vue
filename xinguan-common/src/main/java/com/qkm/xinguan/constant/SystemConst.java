package com.qkm.xinguan.constant;

/**
 * @author qiukangming
 * @version 1.0
 * @description 用户常量信息类
 * @since 2020/09/25 14:47
 */

@SuppressWarnings("unused")
public class SystemConst {

    // 性别相关
    public static final Integer MALE = 0;
    public static final Integer FEMALE = 1;

    // 状态相关
    public static final Integer FORBIDDEN = 0;
    public static final Integer ACTIVE = 1;

    // 密码相关
    public static final String DEFAULT_PASSWORD = "123456";

    // 管理员相关
    public static Integer ADMIN = 0;
    public static Integer SYSTEM_ACCOUNT = 1;

    // 菜单相关
    public static Long ROOT_MENU = 0L;
    public static Integer OPEN = 1;
    public static String MENU = "0";
    public static String BUTTON = "1";
}
