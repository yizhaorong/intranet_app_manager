package org.yzr.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yzr.model.User;
import org.yzr.service.AppService;
import org.yzr.utils.file.PathManager;
import org.yzr.utils.response.BaseResponse;
import org.yzr.utils.response.ResponseUtil;
import org.yzr.vo.AppViewModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {

    @Resource
    private AppService appService;
    @Resource
    private PathManager pathManager;

    @RequiresAuthentication
    @GetMapping("/apps")
    public String apps(HttpServletRequest request) {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            User user = (User) currentUser.getPrincipal();
            List<AppViewModel> apps = this.appService.findByUser(user, request);
            request.setAttribute("apps", apps);
            request.setAttribute("baseURL", PathManager.request(request).getBaseURL());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }

    @RequiresPermissions("/apps/get")
    @GetMapping("/apps/{appID}")
    public String getAppById(@PathVariable("appID") String appID, HttpServletRequest request) {
        Subject currentUser = SecurityUtils.getSubject();
        User user = (User) currentUser.getPrincipal();
        AppViewModel appViewModel = this.appService.getById(appID, user, request);
        request.setAttribute("package", appViewModel);
        request.setAttribute("apps", appViewModel.getPackageList());
        return "list";
    }

    @RequiresPermissions("/packageList/get")
    @RequestMapping("/packageList/{appID}")
    @ResponseBody
    public Map<String, Object> getAppPackageList(@PathVariable("appID") String appID, HttpServletRequest request) {
        Subject currentUser = SecurityUtils.getSubject();
        User user = (User) currentUser.getPrincipal();
        AppViewModel appViewModel = this.appService.getById(appID, user, request);
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("packages", appViewModel.getPackageList());
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

//    @RequiresPermissions("/app/delete")
    @RequestMapping("/app/delete/{id}")
    @ResponseBody
    public BaseResponse deleteById(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            User user = (User) currentUser.getPrincipal();
            if (user == null) {
                return ResponseUtil.unauthz();
            }
            AppViewModel viewModel = this.appService.getById(id, user, request);
            if (viewModel.getUserId().equals(user.getId())) {
                this.appService.deleteById(id);
                return ResponseUtil.ok("删除成功");
            }
            return ResponseUtil.unauthz();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail();
        }
    }

}
