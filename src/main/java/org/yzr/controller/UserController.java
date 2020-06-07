package org.yzr.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yzr.model.User;
import org.yzr.service.UserService;
import org.yzr.utils.IpUtil;
import org.yzr.utils.response.BaseResponse;
import org.yzr.utils.response.ResponseUtil;

import javax.servlet.http.HttpServletRequest;

import static org.yzr.utils.response.ResponseCode.USER_INVALID_ACCOUNT;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/account/login")
    @ResponseBody
    public BaseResponse login(@RequestBody User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }
        return login(request, username, password);
    }

    @NotNull
    private BaseResponse login(HttpServletRequest request, String username, String password) {
        User user;
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            return ResponseUtil.fail(USER_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            return ResponseUtil.fail(USER_INVALID_ACCOUNT, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            return ResponseUtil.fail(USER_INVALID_ACCOUNT, "认证失败");
        }
        user = (User) currentUser.getPrincipal();
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        user.setLastLoginTime(System.currentTimeMillis());
        userService.updateById(user);
        return ResponseUtil.ok();
    }

    @PostMapping("/account/register")
    @ResponseBody
    public BaseResponse register(@RequestBody User user, HttpServletRequest request) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return ResponseUtil.badArgument();
        }
        User u = this.userService.findByUsername(username);
        if (u != null) {
            return ResponseUtil.fail(100, username + "已被注册");
        }
        user = this.userService.createUser(username, password);
        return login(request, username, password);
    }

    @GetMapping("/account/logout")
    @ResponseBody
    public BaseResponse logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ResponseUtil.ok();
    }

    @GetMapping("/account/signin")
    public String signin(HttpServletRequest request) {
        return "signin";
    }

    @GetMapping("/account/signup")
    public String signup(HttpServletRequest request) {
        return "signup";
    }
}
