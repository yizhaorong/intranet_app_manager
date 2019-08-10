package org.yzr.controller;


import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.service.AppService;
import org.yzr.service.PackageService;
import org.yzr.utils.PathManager;
import org.yzr.utils.ipa.PlistGenerator;
import org.yzr.vo.AppViewModel;
import org.yzr.vo.PackageViewModel;
import org.yzr.vo.WebHookViewModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebHookController {
    @Resource
    private AppService appService;
    @Resource
    private PackageService packageService;
    @Resource
    private PathManager pathManager;

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
        return result;
    }

}
