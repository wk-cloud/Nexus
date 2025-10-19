package com.nexus.framework.aspect;

import com.nexus.common.utils.IpUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.utils.TokenUtils;
import com.nexus.common.annotation.OperationLog;
import com.nexus.system.domain.SysOperationLog;
import com.nexus.system.domain.vo.SysUserVo;
import com.nexus.system.service.SysOperationLogService;
import com.nexus.system.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 操作日志切面
 * @author wk
 * @date  2022/11/5
 */
@Aspect
@Slf4j
@Order(2)
@Component
public class OperationLogAspect {

    @Resource
    private SysOperationLogService sysOperationLogService;
    @Resource
    private SysUserService sysUserService;

    /**
     * 切面
     * 这里直接将上一步的切点和环绕通知整合到了一起
     *
     * @return {@link Object}
     */
    @Around("@annotation(operationLog)")
    public Object operationLog(ProceedingJoinPoint proceedingJoinPoint, OperationLog operationLog) throws Throwable {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();

        // 获取注解标注的方法
        Method method = signature.getMethod();

        // 获取注解标注的方法所在的类
        Object target = proceedingJoinPoint.getTarget();
        RequestMapping requestMapping = target.getClass().getAnnotation(RequestMapping.class);
        Tag tag = target.getClass().getAnnotation(Tag.class);

        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取token
        String token = request.getHeader("Authorization");

        // 获取用户id和用户名
        Long userId = Long.parseLong((String) TokenUtils.getValueFromToken(token, "userId"));
        String username = (String) TokenUtils.getValueFromToken(token, "userName");
        if (StringUtils.isBlank(username)) {
            SysUserVo sysUserVo = sysUserService.queryVoById(userId);
            username = sysUserVo.getNickName();
            if (StringUtils.isBlank(username)) {
                username = sysUserVo.getUsername();
            }
        }

        // 获取ip地址
        String ipAddress = IpUtils.getIpAddress(request);

        // 获取基础模块请求路径
        String baseUrl = requestMapping.value()[0];

        // 获取模块名
        String module = operationLog.module();
        if (ObjectUtils.isNotNull(tag) && StringUtils.isNotBlank(tag.name())) {
            module = tag.name();
        }

        // 处理被日志注解标注的方法的注解参数，获取部分请求路径和请求方式
        Map<String, String> requestUrlAndRequestType = this.getRequestUrlAndRequestType(method);
        // 请求地址
        String url = requestUrlAndRequestType.get("url");
        // 请求方式
        String requestType = requestUrlAndRequestType.get("requestType");

        if(StringUtils.isBlank(url)){
            url = operationLog.requestUrl();
        }

        if(StringUtils.isBlank(requestType)){
            requestType = operationLog.requestType();
        }

        // 环绕后通知，获取响应结果
        Object result = proceedingJoinPoint.proceed();

        // 保存日志信息
        SysOperationLog log = new SysOperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ipAddress);
        log.setModule(module);
        log.setOperationType(operationLog.operationType().name());
        log.setOperationDesc(operationLog.operationDesc());
        log.setRequestType(requestType);
        log.setRequestUrl(baseUrl + url);
        log.setRequestMethod(method.toString());
        log.setRequestParams(Arrays.asList(proceedingJoinPoint.getArgs()).toString());
        log.setRequestResult(result.toString());
        sysOperationLogService.save(log);
        return result;

        //log.info("被增强的方法所在的类：" + proceedingJoinPoint.getTarget());
        //log.info("注解标注的方法：" + method);
        //log.info("模块名：" + blogLog.module());
        //log.info("接口名：" + blogLog.desc());
        //log.info("请求方式：" + blogLog.request());
        //log.info("方法名称：" + method.getName());
        //log.info("方法参数类型：" + method.getParameters());
        //log.info("方法参数值：" + Arrays.asList(proceedingJoinPoint.getArgs()));
        //log.info("方法上标注的注解：" + Arrays.asList(method.getDeclaredAnnotations()));
        //log.info("方法返回结果" + method.invoke(proceedingJoinPoint.getTarget(),proceedingJoinPoint.getArgs()));
    }

    /**
     * 获取请求url和请求类型
     *
     * @return {@link Map}<{@link String},{@link String}>
     */
    private Map<String, String> getRequestUrlAndRequestType(Method method) {
        String requestType = "";
        String url = "";
        if (method.isAnnotationPresent(GetMapping.class)) {
            requestType = RequestMethod.valueOf("GET").name();
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            if (getMapping.value().length > 0) {
                url = getMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            requestType = RequestMethod.valueOf("POST").name();
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            if (postMapping.value().length > 0) {
                url = postMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            requestType = RequestMethod.valueOf("DELETE").name();
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            if (deleteMapping.value().length > 0) {
                url = deleteMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            requestType = RequestMethod.valueOf("PUT").name();
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            if (putMapping.value().length > 0) {
                url = putMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            requestType = annotation.method()[0].name();
            if (annotation.value().length > 0) {
                url = annotation.value()[0];
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("url",url);
        map.put("requestType",requestType);
        return map;
    }

}
