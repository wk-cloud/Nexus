package com.nexus.framework.shiro.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.nexus.common.annotation.Pass;
import com.nexus.common.core.view.Result;
import com.nexus.common.enums.HttpCodeEnum;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.framework.config.ShiroConfig;
import com.nexus.framework.shiro.CustomerAuthenticationToken;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 访问控制过滤器
 *
 * @author wk
 * @date 2022/8/31
 */
@Slf4j
public class CustomerAccessControlFilter extends BasicHttpAuthenticationFilter {

    /**
     * 得到servlet上下文
     *
     * @return {@link ServletContext}
     */
    private ServletContext getServletContext(ServletRequest request) {
        return request.getServletContext();
    }

    /**
     * 得到过滤器链允许放行的 url 列表
     * @param request 请求
     * @return {@link List}<{@link String}>
     */
    private List<String> getFilterChainIsAccessAllowedUrlList (ServletRequest request){
        ServletContext servletContext = this.getServletContext(request);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        List<String> allowReleaseUrlList = new ArrayList<>();
        if(ObjectUtils.isNotNull(webApplicationContext)){
            ShiroConfig shiroConfig = (ShiroConfig) webApplicationContext.getBean("shiroConfig");
            allowReleaseUrlList = shiroConfig.getIsAccessAllowedUrlList();
        }
        return allowReleaseUrlList;
    }


    /**
     * 前置拦截
     * @param request 请求
     * @param response 响应
     * @param mappedValue 映射值
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-control-Allow-Origin", req.getHeader("Origin"));
        res.setHeader("Access-control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        res.setHeader("Access-control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            res.setStatus(HttpStatus.OK.value());
            return false;
        }
        // 判断是否添加放行注解
        if(this.isPass(request)){
            return true;
        }
        // 校验请求，如果允许访问则放行，否则进行拦截
        // 如果请求携带token，则进行登录验证
        if (isLoginAttempt(request, response)) {
            if(this.isAccessAllowed(request,response,mappedValue)){
                return super.onPreHandle(request,response,mappedValue);
            }
        }else {
            if(this.onAccessDenied(request,response)){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是放行接口
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isPass(ServletRequest request){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        RequestMappingHandlerMapping mapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        HandlerExecutionChain handler;
        try {
            handler = mapping.getHandler(httpServletRequest);
            if(ObjectUtils.isNull(handler)){
                return false;
            }
            HandlerMethod handlerMethod = (HandlerMethod)handler.getHandler();
            Class<?> handlerMethodClass = handlerMethod.getBeanType();
            Pass classWithPassAnnotation = AnnotationUtils.findAnnotation(handlerMethodClass, Pass.class);
            if(ObjectUtils.isNotNull(classWithPassAnnotation)){
                return true;
            }
            Pass methodWithPassAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Pass.class);
            if(ObjectUtils.isNotNull(methodWithPassAnnotation)){
                return true;
            }
        } catch (Exception e) {
            log.error("isPass error:{}",e.getMessage());
        }
        return false;
    }

    /**
     * 判断是否需要进行登录认证（判断请求中是否携带token）
     *
     * @param request  请求
     * @param response 响应
     * @return boolean
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        return StringUtils.isNotBlank(token);
    }


    /**
     * 执行登录
     * executeLogin 会先调用createToken来获取token，这里重写这个方法，就不会自动去调用createToken来获取token，而是使用自定义的token
     *
     * @param request  请求
     * @param response 响应
     * @return boolean
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        CustomerAuthenticationToken customerAuthenticationToken = new CustomerAuthenticationToken(token);
        //交给自定义的realm对象去登录，如果出现错误会抛出异常并被捕获
        getSubject(request, response).login(customerAuthenticationToken);
        return true;
    }

    /**
     * 是否允许访问
     * 是否允许访问，如果携带token，则进行token校验，否则直接放行
     * 用来处理携带token的请求
     * @param servletRequest  servlet 请求
     * @param servletResponse Servlet 响应
     * @param o               o
     * @return boolean
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) {
        // 需要进行登录认证的请求
        try {
            return executeLogin(servletRequest, servletResponse);
        } catch (Exception e) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().write(JSONUtils.toJSONString(Result.fail(HttpCodeEnum.TOKEN_EXPIRED.getCode(),"登录信息失效，请重新登录")));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 这个方法用来执行登录认证
     * 用来处理没有携带token的请求
     *
     * @param servletRequest  servlet 请求
     * @param servletResponse Servlet 响应
     * @return boolean
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) {
        // 如果没有token，则可能是尝试登录状态，或者游客模式浏览，如果是前台接口直接放行，如果是后台接口则禁止访问
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 当前请求的url地址
        String url = request.getServletPath();
        // 是否允许放行
        boolean isAccessAllowed = false;
        // 获取的允许放行的url地址
        List<String> filterChainAllowReleaseUrlList = this.getFilterChainIsAccessAllowedUrlList(servletRequest);
        if(CollectionUtils.isEmpty(filterChainAllowReleaseUrlList)){
            return false;
        }
        if(filterChainAllowReleaseUrlList.contains(url)){
            isAccessAllowed = true;
        }
        if (!isAccessAllowed) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().write(JSONUtil.toJsonStr(Result.fail(HttpCodeEnum.NO_PERMISSION.getCode(),"权限不足")));
            } catch (IOException e) {
               log.error("onAccessDenied error:{}",e.getMessage());
            } finally {
                return false;
            }
        }
        return true;
    }
}
