package com.nexus.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Spring MVC 工具
 *
 * @author wk
 * @date 2025/03/29
 */
public class SpringMvcUtils {

    /**
     * 获取请求
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            throw (new RuntimeException("非Web上下文无法获取Request"));
        } else {
            return servletRequestAttributes.getRequest();
        }
    }

    /**
     * 获取响应
     *
     * @return {@link HttpServletResponse}
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            throw (new RuntimeException("非Web上下文无法获取Response"));
        } else {
            return servletRequestAttributes.getResponse();
        }
    }

    /**
     * 是 Web
     *
     * @return boolean
     */
    public static boolean isWeb() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

}
