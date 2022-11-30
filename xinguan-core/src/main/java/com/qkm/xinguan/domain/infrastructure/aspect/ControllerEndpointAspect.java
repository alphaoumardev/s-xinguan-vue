package com.qkm.xinguan.domain.infrastructure.aspect;

import cn.hutool.json.JSONUtil;
import com.qkm.xinguan.domain.entity.Log;
import com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.AddressUtil;
import com.qkm.xinguan.domain.infrastructure.utils.IpUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/31 23:21
 * @description
 */

@Slf4j
@Aspect
@Component
public class ControllerEndpointAspect extends AspectSupport {

    private final Log sysLog = new Log();

    private final LogRepository logRepository;

    @Autowired
    public ControllerEndpointAspect(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Pointcut("@annotation(com.qkm.xinguan.domain.infrastructure.annotation.ControllerEndpoint)")
    public void pointcut() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint 切入点
     */
    @Around("pointcut()")
    public Object saveSysLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        // 开始时间
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        ControllerEndpoint controllerEndpoint = method.getAnnotation(ControllerEndpoint.class);
        if (controllerEndpoint != null) {
            String operation = controllerEndpoint.operation();
            // 注解上的操作描述
            sysLog.setOperation(operation);
        }

        // 请求的参数
        Object[] args = joinPoint.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        sysLog.setParams("paramName:" + Arrays.toString(paramNames) + ",args:" + Arrays.toString(args));

        // 请求的IP
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ipAddr = IpUtil.getIpAddr(request);
        sysLog.setIp(ipAddr);

        // 地理位置
        sysLog.setLocation(AddressUtil.getCityInfo(ipAddr));
        // 操作人
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        if (Objects.isNull(loginUser)){
            sysLog.setUsername("[SYSTEM]匿名用户");
        } else {
            sysLog.setUsername(loginUser.getUsername());
        }

        //执行目标方法
        result = joinPoint.proceed();

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        //请求结果
        String resultStr = "response:" + postHandle(result);
        sysLog.setResult(resultStr);

        //执行耗时
        sysLog.setTime(BigDecimal.valueOf(System.currentTimeMillis() - startTime));

        //保存系统日志
        logRepository.saveLog(sysLog);
        return result;
    }

    /**
     * 返回数据
     *
     * @param retVal 数据
     * @return String， json格式
     */
    private String postHandle(Object retVal) {
        if (Objects.isNull(retVal)) {
            return "{}";
        }
        return JSONUtil.toJsonStr(retVal);
    }
}
