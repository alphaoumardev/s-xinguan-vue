package com.qkm.xinguan.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.transform.dto.LoginDTO;
import com.qkm.xinguan.domain.transform.dto.RoleIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.UserChgPwdDTO;
import com.qkm.xinguan.domain.transform.form.UserSearchForm;
import com.qkm.xinguan.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qkm.xinguan.vo.UserVo;
import com.qkm.xinguan.response.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 用户表 服务类
 *
 * @author qiukangming
 * @since 2020-09-24
 */

public interface UserRepository extends IService<User> {
    /**
     * 分页获取用户列表
     *
     * @param page 页数
     * @param size 条数
     * @return Page<User>
     */
    Page<UserVo> getUsersListPage(Integer page, Integer size);

    /**
     * 条件查询用户列表，并且分页
     *
     * @param userSearchForm 用户查询框
     * @param page           页码
     * @param size           数量
     * @return Page<UserVO>
     */
    Page<UserVo> searchUserListPage(UserSearchForm userSearchForm, Integer page, Integer size);

    /**
     * 根据用户id启用或禁用账户
     *
     * @param id     用户id
     * @param status 状态
     * @return 影响的行数
     */
    int updateUserStatusById(Long id, Integer status);

    /**
     * 用户登录
     *
     * @param loginDTO 用户名密码传输
     * @param request 记录信心
     * @return Result
     */
    @Deprecated
    Result login(LoginDTO loginDTO, HttpServletRequest request);

    /**
     * 根据用户 id 重置账户密码
     *
     * @param id 账户id
     * @return 影响行数
     */
    int resetPassword(Long id);

    /**
     * 根据用户名获取用户头像
     *
     * @param username 用户名
     * @return String
     */
    String getUserAvatarByUsername(String username);

    /**
     * 检测用户名是否存在
     *
     * @param username 用户名
     * @return boolean
     */
    boolean checkUserExist(String username);

    /**
     * 查询用户列表信息
     *
     * @param userSearchForm 查询条件对象
     * @return List<UserVo>
     */
    List<UserVo> getUserList(UserSearchForm userSearchForm);

    /**
     * 修改自己的密码
     *
     * @param userChgPwdDTO userChgPwdDTO
     * @return Result
     */
    Result resetSelfPwd(UserChgPwdDTO userChgPwdDTO);

    /**
     * 获取验证码图片
     *
     * @param historyId historyId
     * @return Result
     */
    Result getCaptchaImage(String historyId) throws IOException;

    /**
     * 导出用户列表
     *
     * @param userSearchForm 查询对象
     * @param response       响应头
     */
    void exportUserList(HttpServletResponse response, UserSearchForm userSearchForm);

    /**
     * 获取用户所拥有的角色列表
     *
     * @param id 用户id
     * @return List<Long
     */
    List<Long> getRolesById(Long id);

    /**
     * 给用户分配角色
     *
     * @param id 用户 id
     * @param roleIds 角色 ids 数据包
     * @return Result
     */
    Result assignRoles(Long id, RoleIdsBodyDTO roleIds);

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return User
     */
    User getByUserName(String username);
}
