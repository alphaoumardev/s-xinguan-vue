package com.qkm.xinguan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.WordUtil;
import com.qkm.xinguan.domain.transform.dto.LoginDTO;
import com.qkm.xinguan.domain.transform.dto.RoleIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.UserChgPwdDTO;
import com.qkm.xinguan.domain.transform.dto.UserDTO;
import com.qkm.xinguan.domain.transform.form.UserSearchForm;
import com.qkm.xinguan.domain.entity.User;
import com.qkm.xinguan.exception.BindingResultException;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.oss.OssEntity;
import com.qkm.xinguan.repository.OssRepository;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.repository.UserRepository;
import com.qkm.xinguan.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 用户表 前端控制器, 用户管理相关接口
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@RestController
public class UserController implements IUserController {

    private final UserRepository userRepository;

    private final OssRepository ossRepository;

    private final OssEntity ossEntity;

    @Autowired
    public UserController(UserRepository userRepository, OssRepository ossRepository, OssEntity ossEntity) {
        this.userRepository = userRepository;
        this.ossRepository = ossRepository;
        this.ossEntity = ossEntity;
    }

    @Override
    public Result getOneById(Long id) {
        User user = userRepository.getById(id);
        if (null != user) {
            return Result.ok().data(user);
        } else {
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
    }

    @Override
    @Deprecated
    public Result login(@RequestBody LoginDTO loginDTO, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return userRepository.login(loginDTO, request);
    }

    @Override
    public Result getUsersListPage(Integer page, Integer size) {
        // 对用户进行分页
        Page<UserVo> pageInfo = userRepository.getUsersListPage(page, size);
        return Result.ok().data(pageInfo);
    }

    @Override
    public Result searchUserListPage(@RequestBody UserSearchForm userSearchForm, Integer page, Integer size) {
        Page<UserVo> pageInfo = userRepository.searchUserListPage(userSearchForm, page, size);
        return Result.ok().data(pageInfo);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "更新用户信息[启用/禁用]失败", operation = "用户管理[启/禁用用户]")
    public Result updateUserStatus(@RequestParam("id") Long id, @RequestParam("forbidden") Boolean forbidden) {
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        if (loginUser.getId().equals(id)) {
            throw new BusinessException("不能操作当前登录用户！");
        }
        User user = userRepository.getById(id);
        if (SystemConst.ADMIN.equals(user.getType())) {
            throw new BusinessException("不能禁用管理员！");
        }
        int res = userRepository.updateUserStatusById(id, forbidden ? SystemConst.FORBIDDEN : SystemConst.ACTIVE);
        String msg = forbidden ? "禁用" : "启用";
        if (res == 1) {
            return Result.ok().message(msg + "账户成功");
        } else {
            throw new BusinessException(ResultCode.FORBIDDEN_ACCOUNT.getCode(), msg + "账户失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "重置用户密码失败", operation = "用户管理[重置密码]")
    public Result resetPassword(Long id) {
        int res = userRepository.resetPassword(id);
        if (res == 1) {
            return Result.ok().message("重置账户密码成功");
        } else {
            throw new BusinessException(ResultCode.REST_ACCOUNT_PWD.getCode(), "重置账户密码失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "添加用户信息失败", operation = "用户管理[添加]")
    public Result addUser(@RequestBody @Validated UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        // 检查用户是否存在
        if (userRepository.checkUserExist(userDTO.getUsername())) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_ALREADY_EXIST);
        }
        // 检查用户名是否含有中文
        if (WordUtil.isContainChinese(userDTO.getUsername())){
            throw new BusinessException(ResultCode.ACCOUNT_HAS_CHINESE);
        }
        boolean b = userRepository.save(UserUtil.executeEntity(userDTO, null));
        if (b) {
            return Result.ok().message("用户添加成功");
        } else {
            throw new BusinessException(ResultCode.ADD_ACCOUNT.getCode(), "添加用户失败");
        }
    }

    @Override
    public Result getUserAvatar(String username) {
        String avatar = userRepository.getUserAvatarByUsername(username);
        return Result.ok().message("获取用户头像成功").data(avatar);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "编辑用户信息失败", operation = "用户管理[修改]")
    public Result editUser(@RequestBody @Validated UserDTO userDTO, Long id, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        User user = userRepository.getById(id);
        if (!user.getUsername().equals(userDTO.getUsername())){
            // 检查用户是否存在
            boolean userExist = userRepository.checkUserExist(userDTO.getUsername());
            if (userExist) {
                return Result.error(ResultCode.USER_ACCOUNT_ALREADY_EXIST);
            }
        }
        // 检查用户名是否含有中文
        if (WordUtil.isContainChinese(userDTO.getUsername())){
            return Result.error(ResultCode.ACCOUNT_HAS_CHINESE);
        }
        boolean b = userRepository.updateById(UserUtil.executeEntity(userDTO, id));
        if (b) {
            return Result.ok().message("修改用户信息成功");
        } else {
            throw new BusinessException(ResultCode.EDIT_ACCOUNT.getCode(), "修改用户信息失败");
        }
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "删除用户信息失败", operation = "用户管理[删除]")
    public Result deleteUser(Long id) {
        User user = userRepository.getById(id);
        if (Objects.isNull(user)){
            throw new BusinessException(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        if (loginUser.getId().equals(id)) {
            throw new BusinessException("不能操作当前登录用户！");
        }
        if (user.getType().equals(SystemConst.ADMIN)) {
            throw new BusinessException("不能删除系统管理员！");
        }
        boolean res = userRepository.removeById(id);
        if (res) {
            if (!StringUtils.isEmpty(user.getAvatar())){
                String fileName = user.getAvatar().replaceFirst(ossEntity.getUrlPrefix(), "");
                ossRepository.deleteFile(fileName);
            }
            return Result.ok().message("删除用户信息成功");
        }
        throw new BusinessException("删除用户信息失败");
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "导出用户信息列表失败", operation = "用户管理[导出]")
    public void exportUserList(HttpServletResponse response, @RequestBody UserSearchForm userSearchForm, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        userRepository.exportUserList(response, userSearchForm);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "修改自己的密码失败", operation = "用户管理[修改密码]")
    public Result resetSelfPwd(@RequestBody @Validated UserChgPwdDTO userChgPwdDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new BindingResultException(result);
        }
        return userRepository.resetSelfPwd(userChgPwdDTO);
    }

    @Override
    public Result getCaptchaImage(String historyId) throws IOException {
        return userRepository.getCaptchaImage(historyId);
    }

    @Override
    public Result getRolesById(Long id) {
        List<Long> roleIds = userRepository.getRolesById(id);
        return Result.ok().data(roleIds);
    }

    @Override
    @ControllerEndpoint(exceptionMessage = "为用户赋予角色失败", operation = "用户管理[用户赋权]")
    public Result assignRoles(Long id, @RequestBody RoleIdsBodyDTO roleIds) {
        return userRepository.assignRoles(id, roleIds);
    }
}

