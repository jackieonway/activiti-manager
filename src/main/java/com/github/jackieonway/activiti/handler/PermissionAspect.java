package com.github.jackieonway.activiti.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Jackie
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Pointcut("execution(public * com.github.jackieonway.activiti.controller.*.*.*(..))")
    public void permission() {
    }

    @Before("permission()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(sra)) {
            throw new BusinessException("服务器异常");
        }
        HttpServletRequest request = sra.getRequest();
        Object[] objects = joinPoint.getArgs();
        log.debug("请求接口[{}]对应的controller [{}.{}] 方法开始执行...    参数信息如下 : [{}]", request.getRequestURL(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(objects));
    }
}