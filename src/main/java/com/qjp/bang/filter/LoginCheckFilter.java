package com.qjp.bang.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 * @author qjp
 */
@Component
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest)servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;
//        1、获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
//        2、判断本次请求是否需要处理
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/**"
        };
        boolean check = check(urls,requestURI);
//        3、如果不需要处理，则直接放行
        if (check){
            //request.getLocalAddr()//获取本机IP
            //request.getRemoteAddr()//Java获取远程IP地址:
            String remoteAddr = request.getRemoteAddr();
            String remoteHost = request.getRemoteHost();
            String queryString = request.getQueryString();
//            log.info("ip:[{}]",remoteAddr);
//            log.info("主机名:[{}]",remoteHost);
//            log.info("本次请求{}不需要处理",requestURI);
            log.info("请求参数：[{}]",queryString);
            filterChain.doFilter(request,response);
        }
////        4-1、判断登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("employee") != null){
//            log.info("用户{}已登录！",request.getSession().getAttribute("employee"));
//
//            Long employeeId = (Long) request.getSession().getAttribute("employee");
//
//            BaseContext.setCurrentId(employeeId);
//
//            filterChain.doFilter(request,response);
//            return;
//        }
////        4-2、判断登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("user") != null){
//            log.info("用户{}已登录！",request.getSession().getAttribute("user"));
//
//            Long userId = (Long) request.getSession().getAttribute("user");
//
//            BaseContext.setCurrentId(userId);
//
//            filterChain.doFilter(request,response);
//            return;
//        }
//        5、如果未登录则返回未登录结果,通过输出流的方式向客户端响应数据
//        log.info("用户未登录！");
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check (String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }return false;
    }
}
