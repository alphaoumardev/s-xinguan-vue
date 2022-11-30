package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.Role;
import com.qkm.xinguan.domain.entity.UserRole;
import com.qkm.xinguan.domain.transform.dto.LoginDTO;
import com.qkm.xinguan.domain.transform.dto.RoleIdsBodyDTO;
import com.qkm.xinguan.domain.transform.dto.UserChgPwdDTO;
import com.qkm.xinguan.domain.transform.form.UserSearchForm;
import com.qkm.xinguan.domain.entity.User;
import com.qkm.xinguan.domain.infrastructure.config.CustomCacheConfig;
import com.qkm.xinguan.domain.infrastructure.mapper.UserMapper;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.domain.infrastructure.utils.VerifyCodeUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.vo.UserExportVo;
import com.qkm.xinguan.vo.UserVo;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */

@Slf4j
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {

    private final DepartmentRepository departmentRepository;

    private final CustomCacheRepository customCacheRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final LoginLogRepository loginLogRepository;

    @Autowired
    public UserRepositoryImpl(DepartmentRepository departmentRepository, CustomCacheRepository customCacheRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, LoginLogRepository loginLogRepository) {
        this.departmentRepository = departmentRepository;
        this.customCacheRepository = customCacheRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    public Page<UserVo> getUsersListPage(Integer page, Integer size) {
        Page<User> p = this.baseMapper.selectPage(new Page<>(page, size), null);
        return toUserVOPage(p);
    }

    @Override
    public Page<UserVo> searchUserListPage(UserSearchForm userSearchForm, Integer page, Integer size) {
        LambdaQueryWrapper<User> wrapper = executeWrapper(userSearchForm);
        Page<User> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), wrapper);
        return toUserVOPage(pageInfo);
    }

    @Override
    public int updateUserStatusById(Long id, Integer status) {
        return this.baseMapper.updateUserStatusById(id, status);
    }

    @Override
    @Deprecated
    public Result login(LoginDTO loginDTO, HttpServletRequest request) {
        // 首先先验证验证码
        Long interval = customCacheRepository.getKeyInterval(CustomCacheConfig.LOGIN_VERIFY_CODE + loginDTO.getVerifyCodeId());
        if (interval == null || interval <= 3) {
            return Result.error().message("验证码已过期, 请重新点击图片生成新的验证码！");
        }
        String code = customCacheRepository.getLoginVerifyCode(loginDTO.getVerifyCodeId());
        if (!loginDTO.getVerifyCode().equalsIgnoreCase(code)) {
            return Result.error().message("验证码错误, 请重试！");
        }
        User user = this.baseMapper.getByUsername(loginDTO.getUsername());
        if (null == user) {
            return Result.error(ResultCode.USER_ACCOUNT_NOT_EXIST);
        }
        if (user.getForbidden().equals(SystemConst.FORBIDDEN)) {
            return Result.error(ResultCode.USER_ACCOUNT_LOCKED);
        }
        loginLogRepository.saveLoginLog(loginDTO.getUsername(), request);
        //正常用户，比较密码
        if (UserUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            String deptName = departmentRepository.getDeptNameById(user.getDepartmentId());
            customCacheRepository.deleteLoginVerifyCode(loginDTO.getVerifyCodeId());
            return Result.ok().data(UserUtil.executeVo(user, deptName));
        } else {
            return Result.error(ResultCode.USER_CREDENTIALS_ERROR);
        }
    }

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return User
     */
    @Override
    public User getByUserName(String username) {
        return this.baseMapper.getByUsername(username);
    }
    @Override
    public int resetPassword(Long id) {
        return this.baseMapper.resetPassword(UserUtil.encode(SystemConst.DEFAULT_PASSWORD), id);
    }

    @Override
    public String getUserAvatarByUsername(String username) {
        return this.baseMapper.getUserAvatarByUsername(username);
    }

    @Override
    public boolean checkUserExist(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<UserVo> getUserList(UserSearchForm userSearchForm) {
        LambdaQueryWrapper<User> wrapper = executeWrapper(userSearchForm);
        List<User> users = this.baseMapper.selectList(wrapper);
        return toUserVoList(users);
    }

    /**
     * 修改自己的密码
     *
     * @param userChgPwdDTO userChgPwdDTO
     * @return Result
     */
    @Override
    public Result resetSelfPwd(UserChgPwdDTO userChgPwdDTO) {
        // 比较密码
        if (userChgPwdDTO.getOldPwd().equals(userChgPwdDTO.getNewPwd())) {
            return Result.error().message("新密码和旧密码一致");
        }
        // 查询用户
        User user = this.baseMapper.selectById(userChgPwdDTO.getId());
        if (Objects.isNull(user)) {
            return Result.error().message("用户信息错误[ 用户不存在 ]");
        }
        // 比较数据库密码和旧密码
        if (!UserUtil.matches(userChgPwdDTO.getOldPwd(), user.getPassword())) {
            return Result.error().message("旧密码错误[ 与原密码不一致 ]");
        }
        // 符合修改的条件
        int i = this.baseMapper.resetPassword(UserUtil.encode(userChgPwdDTO.getNewPwd()), user.getId());
        if (i > 0) {
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.FAILED);
        }
    }

    /**
     * 获取验证码图片
     *
     * @return Result
     */
    @Override
    public Result getCaptchaImage(String historyId) throws IOException {
        // 生成随机字串
        String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
        // 唯一标识
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (!historyId.equals("first")) {
            customCacheRepository.deleteLoginVerifyCode(historyId);
        }
        customCacheRepository.setLoginVerifyCode(uuid, verifyCode, CustomCacheConfig.LOGIN_VERIFY_CODE_TIME);
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtil.outputImage(w, h, stream, verifyCode);
        String image = Base64.getEncoder().encodeToString(stream.toByteArray());
        stream.close();
        Map<String, String> res = new HashMap<>(2);
        res.put("id", uuid);
        res.put("image", image);
        return Result.ok().data(res);
    }

    /**
     * 导出用户列表
     *
     * @param userSearchForm 查询对象
     */
    @Override
    public void exportUserList(HttpServletResponse response, UserSearchForm userSearchForm) {
        List<UserVo> userList = this.getUserList(userSearchForm);
        List<UserExportVo> userExportVos = UserUtil.toExportVos(userList);
        // 实现导出
        try {
            ExportParams params = new ExportParams("用户信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(userExportVos, UserExportVo.class, "用户信息表", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    /**
     * 获取用户所拥有的角色列表
     *
     * @param id 用户id
     * @return List<Long
     */
    @Override
    public List<Long> getRolesById(Long id) {
        User user = this.baseMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return userRoleRepository.getRoleIdsByUserId(user.getId());
    }

    /**
     * 给用户分配角色
     *
     * @param id   用户 id
     * @param roleIds 角色 ids 数据包
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result assignRoles(Long id, RoleIdsBodyDTO roleIds) {
        Long[] rids = roleIds.getRids();
        User user = this.baseMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        List<Long> roleIdsList = this.getRolesById(id);
        // 删除用户之前所拥有的角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        boolean res = userRoleRepository.remove(wrapper);

        // 如果需要分配角色，进入此循环
        if (!Objects.isNull(rids) && rids.length > 0) {
            List<UserRole> userRoles = new ArrayList<>(rids.length);
            for (Long rid : rids) {
                Role role = roleRepository.getById(rid);
                // 如果角色不存在
                if (Objects.isNull(role)) {
                    throw new BusinessException(ResultCode.ROLE_IDS_ERROR);
                }
                // 如果角色被禁用
                if (role.getForbidden().equals(SystemConst.FORBIDDEN)) {
                    throw new BusinessException(ResultCode.ROLE_IS_FORBIDDEN);
                }
                UserRole ur = new UserRole();
                ur.setUserId(id);
                ur.setRoleId(rid);
                userRoles.add(ur);
            }
            // 批量插入表中
            boolean result = userRoleRepository.saveBatch(userRoles);
            if (result) {
                return Result.ok().message("成功为用户分配角色！");
            } else {
                throw new BusinessException(ResultCode.ASSIGN_ROLES_ERROR);
            }
        }
        if (res || roleIdsList.isEmpty()) {
            return Result.ok().message("清除用户角色列表成功！");
        } else {
            throw new BusinessException(ResultCode.REMOVE_ROLES_ERROR);
        }
    }



    private Page<UserVo> toUserVOPage(Page<User> userPage) {
        Page<UserVo> pageInfo = new Page<>();
        BeanUtils.copyProperties(userPage, pageInfo);
        List<UserVo> userVos = toUserVoList(userPage.getRecords());
        pageInfo.setRecords(userVos);
        return pageInfo;
    }

    private List<UserVo> toUserVoList(List<User> users) {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserVo> userVos = new ArrayList<>();
        // 获取 部门id列表
        List<Long> deptIds = users.stream().map(User::getDepartmentId).collect(Collectors.toList());
        // 根据 部门id列表查询出对应的部门名称
        List<String> deptNames = departmentRepository.getDeptNamesByIds(deptIds);
        AtomicInteger index = new AtomicInteger();
        users.forEach(user -> {
            UserVo userVO = UserUtil.executeVo(user, deptNames.get(index.getAndIncrement()));
            userVos.add(userVO);
        });
        return userVos;
    }

    private static LambdaQueryWrapper<User> executeWrapper(UserSearchForm userSearchForm) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (null != userSearchForm.getDepartmentId()) {
            wrapper.eq(User::getDepartmentId, userSearchForm.getDepartmentId());
        }
        if (!StringUtils.isEmpty(userSearchForm.getUsername())) {
            wrapper.eq(User::getUsername, userSearchForm.getUsername());
        }
        if (!StringUtils.isEmpty(userSearchForm.getEmail())) {
            wrapper.eq(User::getEmail, userSearchForm.getEmail());
        }
        if (!StringUtils.isEmpty(userSearchForm.getSex())) {
            wrapper.eq(User::getSex, userSearchForm.getSex());
        }
        if (!StringUtils.isEmpty(userSearchForm.getNikeName())) {
            wrapper.eq(User::getNickname, userSearchForm.getNikeName());
        }
        if (!StringUtils.isEmpty(userSearchForm.getForbidden())) {
            wrapper.eq(User::getForbidden, userSearchForm.getForbidden());
        }
        return wrapper;
    }

}
