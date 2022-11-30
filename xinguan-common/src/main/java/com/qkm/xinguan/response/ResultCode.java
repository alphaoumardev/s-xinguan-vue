package com.qkm.xinguan.response;

/**
 * @author qiukangming
 * @version 1.0
 * @description 规定:
 * #200表示成功
 * #1001～1999 区间表示参数错误
 * #2001～2999 区间表示用户错误
 * #3001～3999 区间表示部门错误
 * #4001～4999 区间表示接口，业务异常
 * #后面对什么的操作自己在这里注明就行了
 * @since 2020/09/24 11:10
 */
@SuppressWarnings("unused")
public enum ResultCode implements CustomizeResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAILED(400, "操作失败"),

    /**
     * 操作未授权
     */
    NOT_AUTH(401, "您没有操作权限，请联系管理员！"),

    /**
     * 参数错误：1000～1999
     */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_ERROR(1005, "参数错误"),

    /**
     * 用户错误
     */
    USER_NOT_LOGIN(2001, "未登录系统，禁止访问！"),
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),
    USER_CREDENTIALS_ERROR(2003, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2004, "密码过期"),
    USER_ACCOUNT_DISABLE(2005, "账号不可用"),
    USER_ACCOUNT_LOCKED(2006, "账户已被禁用，请联系管理员！"),
    USER_ACCOUNT_NOT_EXIST(2007, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2008, "账号名称已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号在别处登录"),
    USERLIST_NOT_FOUND(2010, "未找到用户列表"),
    FORBIDDEN_ACCOUNT(2011, "禁用账户"),
    REST_ACCOUNT_PWD(2012, "重置账户密码"),
    ADD_ACCOUNT(2013, "用户添加"),
    EDIT_ACCOUNT(2014, "用户编辑"),
    ACCOUNT_LIST_EMPTY(2015, "用户列表为空"),
    ACCOUNT_HAS_CHINESE(2016, "用户名存在中文, 请输入英文字母或者数字"),
    USER_NOT_EXIST(2017, "该用户不存在"),
    USER_SESSION_INVALID(2018, "登录过期，请重新登录！"),
    LOGOUT_SUCCESS(2019, "注销登录成功！"),
    LOGIN_SUCCESS(2020, "登录成功！"),
    LOGIN_USERNAME_EMPTY(2021, "登录用户名不能为空！"),

    /**
     * 部门错误
     */
    DEPARTMENT_NOT_EXIST(3001, "部门不存在"),
    DEPARTMENT_ALREADY_EXIST(3002, "部门已存在"),
    ADD_DEPT(3003, "部门添加"),
    EDIT_DEPT(3004, "部门编辑"),
    DEL_DEPT_NOT_EMPTY(3005, "部门当前人数不为 0，无法删除"),

    /**
     * 业务错误
     */
    NO_PERMISSION(4001, "没有权限"),
    PAGEINFO_NOT_FOUND(4002, "分页获取的信息为空"),
    ARITHMETIC(4003, "算数错误"),
    API_GET_ERROR(4004, "后台 GET 请求发送错误"),
    REPORT_ERROR(4005, "签到，健康上报错误，请重试！"),
    DOUBLE_REPORT(4006, "今已签到，请勿重复签到！"),
    PRODUCT_IN_STOCK_EMPTY(4007, "物资入库不能为空"),
    PRODUCT_NOT_FOUND(4008,"物资找不到"),
    PRODUCT_WAIT_PASS(4009,"物资等待审核"),
    PRODUCT_STATUS_ERROR(4010,"物资状态错误"),
    PRODUCT_IN_STOCK_NUMBER_ERROR(4011,"物资入库数量非法"),
    PRODUCT_IS_REMOVE(4012,"物资已移入回收站"),

    /**
     * 导出错误
     */
    EXPORT_FAIL(5001, "数据导出异常，请联系管理员！"),

    /**
     * 文件上传
     */
    FILE_DELETE_FAIL(6001, "文件删除失败:"),
    FILE_UPLOAD_FAIL(6002, "文件上传失败:"),

    /**
     * 角色
     */
    ADD_ROLE(7001, "添加角色信息失败！"),
    EDIT_ROLE(7002, "修改角色信息失败！"),
    DELETE_ROLE(7003, "删除角色信息失败！"),
    ROLE_IDS_ERROR(7004, "所传入角色信息有误，请重试！"),
    ROLE_IS_FORBIDDEN(7005, "角色被禁用，无法分配给该用户，请重试！"),
    ASSIGN_ROLES_ERROR(7006, "分配角色失败，请重试！"),
    REMOVE_ROLES_ERROR(7007, "清除用户角色信息失败，请重试！"),
    ROLE_NOT_EXIST(7008, "该角色信息不存在，请重试！"),
    ROLE_ALL_IS_FORBIDDEN(7009, "您所在的所有组织已被禁用，请联系管理员！"),
    DONT_HAVE_ANY_ROLE(7010, "管理员未授予任何权限，请联系管理员！"),

    /**
     * 菜单
     */
    MENU_NOT_EXIST(8001, "该菜单信息不存在，请重试！"),
    MENU_FORBIDDEN(8002, "菜单被禁用，无法分配给该角色，请重试！"),
    ASSIGN_MENUS_ERROR(8003, "分配菜单权限失败，请重试！"),
    REMOVE_MENUS_ERROR(8004, "清除角色菜单权限信息失败，请重试！"),

    /**
     * 凭证错误
     */
    JWT_FORMAT_ERROR(9001, "错误的访问令牌格式！"),
    JWT_IS_EXPIRED(9002, "访问令牌已过期，请重新登录！"),
    JWT_NOT_CURRENT_LOGIN_USER(9003, "非法的访问请求！"),

    /**
     * 系统错误
     */
    NOT_FOUND(9004, "不存在该系统资源！"),
    METHOD_NOT_SUPPERED(9005, "不支持该请求方式！"),
    SYSTEM_ERROR(9006, "系统出现异常，请联系管理员！"),
    UNKNOW_ERROR(9007, "系统发生未知错误，请联系管理员！");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
