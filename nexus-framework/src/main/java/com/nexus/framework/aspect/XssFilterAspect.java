package com.nexus.framework.aspect;


import com.nexus.common.utils.StringEscapeUtils;
import com.nexus.common.annotation.XssFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * XSS 过滤器切面
 *
 * @author wk
 * @date 2025/02/12
 */
@Component
@Aspect
public class XssFilterAspect {

    @Pointcut("@annotation(com.wk.blog.annotation.XssFilter)")
    public void apiPointcut() {
    }

    @Around("apiPointcut()")
    public Object handleXSS(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        int index = 0;
        for (Parameter parameter : parameters) {
            Object arg = args[index];
            if (arg == null) {
                index += 1;
                continue;
            }
            if (arg instanceof String) {
                boolean annotationPresent = parameter.isAnnotationPresent(XssFilter.class) && parameter.getAnnotation(XssFilter.class).enable();
                if(annotationPresent){
                    args[index] = StringEscapeUtils.escapeHtml4(args[index].toString());
                }
            } else {
                Class<?> clazz = arg.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(XssFilter.class) && field.getAnnotation(XssFilter.class).enable()) {
                        field.setAccessible(true);
                        Object value = field.get(arg);
                        if (value instanceof String) {
                            field.set(arg, StringEscapeUtils.escapeHtml4((String) value));
                        }
                    }
                }
            }
            index += 1;
        }
        return joinPoint.proceed(args);
    }

}
