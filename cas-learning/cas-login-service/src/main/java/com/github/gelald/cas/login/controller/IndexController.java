package com.github.gelald.cas.login.controller;

import com.github.gelald.CASConstant;
import com.github.gelald.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYingBin
 * Date 2022/12/22 0022
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @RequestMapping(value = "/toLogin")
    public String toLogin(Model model, HttpServletRequest request) {
        Object userInfo = request.getSession().getAttribute(CASConstant.USER_INFO);
        //不为空，则是已登陆状态
        if (null != userInfo) {
            String ticket = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(ticket, userInfo, 2, TimeUnit.SECONDS);
            return "redirect:" + request.getParameter("url") + "?ticket=" + ticket;
        }
        UserForm user = new UserForm();
        user.setUsername("laowang");
        user.setPassword("laowang");
        user.setBackUrl(request.getParameter("url"));
        model.addAttribute("user", user);

        return "login";
    }

    @RequestMapping("/login")
    public void login(@ModelAttribute UserForm user, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("backUrl:" + user.getBackUrl());
        request.getSession().setAttribute(CASConstant.USER_INFO, user);

        //登陆成功，创建用户信息票据
        String ticket = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(ticket, user, 20, TimeUnit.SECONDS);
        //重定向，回原url  ---a.com
        if (null == user.getBackUrl() || user.getBackUrl().length() == 0) {
            response.sendRedirect("/index");
        } else {
            response.sendRedirect(user.getBackUrl() + "?ticket=" + ticket);
        }
    }

    @GetMapping("/index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Object user = request.getSession().getAttribute(CASConstant.USER_INFO);
        UserForm userInfo = (UserForm) user;
        modelAndView.setViewName("index");
        modelAndView.addObject("user", userInfo);
        request.getSession().setAttribute("test", "123");
        return modelAndView;
    }
}
