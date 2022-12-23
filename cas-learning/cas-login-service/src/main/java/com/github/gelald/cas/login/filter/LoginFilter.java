package com.github.gelald.cas.login.filter;

import com.github.gelald.CASConstant;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author WuYingBin
 * Date 2022/12/22
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Object userInfo = request.getSession().getAttribute(CASConstant.USER_INFO);

        //如果未登陆，则拒绝请求，转向登陆页面
        String requestUrl = request.getServletPath();
        if (!"/toLogin".equals(requestUrl)//不是登陆页面
                && !requestUrl.startsWith("/login")//不是去登陆
                && null == userInfo) {//不是登陆状态

            request.getRequestDispatcher("/toLogin").forward(request, response);
            return;
        }

        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
