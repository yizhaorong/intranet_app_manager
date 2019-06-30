package org.yzr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * 所有错误都转到首页
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.sendRedirect("/apps");
    }
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
