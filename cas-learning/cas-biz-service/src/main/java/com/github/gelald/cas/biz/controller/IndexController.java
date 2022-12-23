package com.github.gelald.cas.biz.controller;

import com.github.gelald.CASConstant;
import com.github.gelald.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WuYingBin
 * Date 2022/12/23 0023
 */
@Controller
@RequestMapping("/biz")
public class IndexController {

    @ResponseBody
    @RequestMapping("fuck-your-mother")
    public String fuck() {
        return "go-fuck-yourself";
    }

    @RequestMapping("/entry")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Object userInfo = request.getSession().getAttribute(CASConstant.USER_INFO);
        UserForm user = (UserForm) userInfo;
        modelAndView.setViewName("home");
//        modelAndView.addObject("user", user);
//        request.getSession().setAttribute("test", "123");
        return modelAndView;
    }
}
