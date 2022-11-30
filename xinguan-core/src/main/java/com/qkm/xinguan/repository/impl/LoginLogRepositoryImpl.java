package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.LoginLog;
import com.qkm.xinguan.domain.infrastructure.mapper.LoginLogMapper;
import com.qkm.xinguan.domain.infrastructure.utils.AddressUtil;
import com.qkm.xinguan.domain.infrastructure.utils.IpUtil;
import com.qkm.xinguan.domain.transform.form.LoginLogSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.LoginLogRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.vo.LoginReportVo;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志表 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class LoginLogRepositoryImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogRepository {

    /**
     * 保存登录日志
     *
     * @param userName 登录用户名
     * @param request 信息
     */
    @Override
    public void saveLoginLog(String userName, HttpServletRequest request) {
        this.baseMapper.insert(createLoginLog(userName, request));
    }

    /**
     * 分页查询登录日志信息
     *
     * @param page               第几页
     * @param size               一页几条数据
     * @param loginLogSearchForm 查询对象
     * @return Page<LoginLog>
     */
    @Override
    public Page<LoginLog> searchLoginLogListPage(Integer page, Integer size, LoginLogSearchForm loginLogSearchForm) {
        return this.baseMapper.selectPage(new Page<>(page, size), executeWrapper(loginLogSearchForm));
    }

    /**
     * 批量删除登录日志
     *
     * @param ids id列表
     * @return Result
     */
    @Override
    public Result batchDeleteLoginLog(String ids) {
        if (StringUtils.isEmpty(ids)){
            throw new BusinessException("批量删除失败，请重试！");
        }
        String[] idsArr = ids.split(",");
        if (idsArr.length <= 0) {
            throw new BusinessException("批量删除的登录日志不能为空！");
        }
        List<Long> idsVal = Arrays.stream(idsArr).map(Long::parseLong).collect(Collectors.toList());
        int delCount = this.baseMapper.deleteBatchIds(idsVal);
        if (idsVal.size() == delCount){
            return Result.ok().message("成功批量删除日志信息！");
        } else if (delCount > 0 && delCount < idsVal.size()) {
            throw new BusinessException("批量删除部分成功，部分失败，原因：未知！");
        } else {
            throw new BusinessException("批量删除失败，请重试！");
        }
    }

    /**
     * 获取登录报表信息
     *
     * @param username 用户名
     * @return List<LoginReportVo>
     */
    @Override
    public List<LoginReportVo> loginReport(String username) {
        return this.baseMapper.loginReport(username);
    }

    private static LoginLog createLoginLog(String loginName, HttpServletRequest request) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(loginName);
        loginLog.setIp(IpUtil.getIpAddr(request));
        loginLog.setLocation(AddressUtil.getCityInfo(IpUtil.getIpAddr(request)));
        // 获取客户端操作系统
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();
        loginLog.setUserSystem(os.getName());
        loginLog.setUserBrowser(browser.getName());
        loginLog.setLoginTime(LocalDateTime.now());
        return loginLog;
    }

    private LambdaQueryWrapper<LoginLog> executeWrapper(LoginLogSearchForm loginLogSearchForm){
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(loginLogSearchForm.getUsername())){
            wrapper.like(LoginLog::getUsername, loginLogSearchForm.getUsername());
        }
        if (!StringUtils.isEmpty(loginLogSearchForm.getIp())){
            wrapper.eq(LoginLog::getIp, loginLogSearchForm.getIp());
        }
        if (!StringUtils.isEmpty(loginLogSearchForm.getLocation())){
            wrapper.like(LoginLog::getLocation, loginLogSearchForm.getLocation());
        }
        wrapper.orderByDesc(LoginLog::getLoginTime);
        return wrapper;
    }
}
