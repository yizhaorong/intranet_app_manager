package org.yzr.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yzr.model.WebHook;
import org.yzr.service.WebHookService;
import org.yzr.vo.WebHookViewModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebHookController {
    @Resource
    private WebHookService webHookService;

    /**
     * 添加webHook
     * @param viewModel
     * @param request
     * @return
     */
    @PostMapping("/webHook/add")
    @ResponseBody
    public Map<String, Object> add(WebHookViewModel viewModel, HttpServletRequest request) {
        Map<String , Object> result = new HashMap<>();
        WebHook webHook = this.webHookService.save(viewModel);
        if (webHook != null) {
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        return result;
    }

    /**
     * 删除webHook
     * @param viewModel
     * @param request
     * @return
     */
    @PostMapping("/webHook/delete")
    @ResponseBody
    public Map<String, Object> delete(WebHookViewModel viewModel, HttpServletRequest request) {
        Map<String , Object> result = new HashMap<>();
        try {
            this.webHookService.deleteById(viewModel.getId());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }

    /**
     * 修改webHook
     * @param viewModel
     * @param request
     * @return
     */
    @PostMapping("/webHook/update")
    @ResponseBody
    public Map<String, Object> update(WebHookViewModel viewModel, HttpServletRequest request) {
        Map<String , Object> result = new HashMap<>();
        try {
            this.webHookService.update(viewModel);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }


    /**
     * 获取 webHook 列表
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/webHook/find/{id}")
    @ResponseBody
    public List<WebHookViewModel> findById(@PathVariable("id") String id, HttpServletRequest request) {
        List<WebHookViewModel> viewModels = new ArrayList<>();
        try {
            List<WebHook> webHookList = this.webHookService.findByAppId(id);
            for (WebHook webHook :
                    webHookList) {
                WebHookViewModel viewModel = new WebHookViewModel();
                BeanUtils.copyProperties(webHook, viewModel);
                viewModels.add(viewModel);
            }
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return viewModels;
    }

}
