package com.qkm.xinguan.controller;

import com.qkm.xinguan.domain.transform.dto.LoginDTO;
import com.qkm.xinguan.domain.transform.dto.RoleIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.UserChgPwdDTO;
import com.qkm.xinguan.domain.transform.dto.UserDTO;
import com.qkm.xinguan.domain.transform.form.UserSearchForm;
import com.qkm.xinguan.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/09/26 22:36
 */

@RequestMapping("/system/user")
@Api(value = "用户管理模块", tags = "用户管理接口")
public interface IUserController {

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:queryOne')")
    @ApiOperation(value = "获取单个用户信息", notes = "根据用户id查询用户信息")
    Result getOneById(@PathVariable("id") Long id);

    @Deprecated
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口", notes = "登录")
    Result login(@RequestBody @Validated LoginDTO loginDTO, BindingResult result, HttpServletRequest request);

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:query')")
    @ApiOperation(value = "分页获取用户列表信息", notes = "分页")
    Result getUsersListPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                            @RequestParam(value = "size", defaultValue = "5") Integer size);

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('user:query')")
    @ApiOperation(value = "查询用户列表信息", notes = "分页查询")
    Result searchUserListPage(@RequestBody UserSearchForm userSearchForm, @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "size", defaultValue = "5") Integer size);

    @PutMapping("/forbidden")
    @PreAuthorize("hasAuthority('user:status')")
    @ApiOperation(value = "修改用户状态信息", notes = "修改用户状态")
    Result updateUserStatus(@RequestParam("id") Long id, @RequestParam("forbidden") Boolean forbidden);

    @PutMapping("/pwd/{id}")
    @PreAuthorize("hasAuthority('user:resetPwd')")
    @ApiOperation(value = "重置账户密码", notes = "重置密码")
    Result resetPassword(@PathVariable("id") Long id);

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:add')")
    @ApiOperation(value = "添加用户", notes = "用户添加")
    Result addUser(@RequestBody @Validated UserDTO userDTO, BindingResult result);

    @GetMapping("/avatar")
    @ApiOperation(value = "获取用户头像", notes = "用户头像")
    Result getUserAvatar(@RequestParam(required = false, value = "username") String username);

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    @ApiOperation(value = "编辑用户", notes = "用户信息修改")
    Result editUser(@RequestBody @Validated UserDTO userDTO, @PathVariable("id") Long id, BindingResult result);

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    @ApiOperation(value = "删除用户", notes = "用户信息删除")
    Result deleteUser(@PathVariable("id") Long id);

    @PostMapping("/export")
    @PreAuthorize("hasAuthority('user:export')")
    @ApiOperation(value = "导出用户列表", notes = "用户列表导出")
    void exportUserList(HttpServletResponse response, @RequestBody UserSearchForm userSearchForm, BindingResult result);

    @PutMapping("/self/pwd")
    @ApiOperation(value = "重置账户密码， 修改自己的密码", notes = "重置密码")
    Result resetSelfPwd(@RequestBody @Validated UserChgPwdDTO userChgPwdDTO, BindingResult result);

    @GetMapping("/captchaImage/{historyId}")
    @ApiOperation(value = "登录获取验证码", notes = "获取验证码")
    Result getCaptchaImage(@PathVariable("historyId") String historyId) throws IOException;

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:queryRoles')")
    @ApiOperation(value = "根据id获取用户所拥有的角色", notes = "获取角色")
    Result getRolesById(@PathVariable("id") Long id);

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    @ApiOperation(value = "为用户分配角色", notes = "用户角色分配")
    Result assignRoles(@PathVariable("id") Long id, @RequestBody RoleIdsBodyDTO roleIds);
}
