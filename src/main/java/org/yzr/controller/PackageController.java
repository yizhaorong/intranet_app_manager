package org.yzr.controller;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.model.Storage;
import org.yzr.model.User;
import org.yzr.service.AppService;
import org.yzr.service.PackageService;
import org.yzr.service.StorageService;
import org.yzr.service.UserService;
import org.yzr.storage.StorageUtil;
import org.yzr.utils.file.FileType;
import org.yzr.utils.file.FileUtil;
import org.yzr.utils.file.PathManager;
import org.yzr.utils.image.QRCodeUtil;
import org.yzr.utils.ipa.PlistGenerator;
import org.yzr.utils.response.BaseResponse;
import org.yzr.utils.response.ResponseUtil;
import org.yzr.utils.webhook.WebHookClient;
import org.yzr.vo.AppViewModel;
import org.yzr.vo.PackageViewModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Controller
public class PackageController {
    @Resource
    private AppService appService;
    @Resource
    private PackageService packageService;
    @Resource
    private PathManager pathManager;
    @Resource
    private UserService userService;
    @Resource
    private StorageUtil storageUtil;
    @Resource
    private StorageService storageService;

    /**
     * 预览页
     *
     * @param code
     * @param request
     * @return
     */
    @GetMapping("/s/{code}")
    public String get(@PathVariable("code") String code, HttpServletRequest request) {
        String id = request.getParameter("id");
        AppViewModel viewModel = this.appService.findByCode(code, id, request);
        request.setAttribute("app", viewModel);
        request.setAttribute("ca_path", PathManager.request(request).getCAPath());
        request.setAttribute("basePath", PathManager.request(request).getBaseURL() + "/");
        return "install";
    }

    /**
     * 设备列表
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/devices/{id}")
    public String devices(@PathVariable("id") String id, HttpServletRequest request) {
        PackageViewModel viewModel = this.packageService.findById(id, request);
        request.setAttribute("app", viewModel);
        return "devices";
    }

    /**
     * 安装教程
     *
     * @param platform
     * @param request
     * @return
     */
    @GetMapping("/guide/{platform}")
    public String guide(@PathVariable("platform") String platform, HttpServletRequest request) {
        request.setAttribute("platform", platform);
        return "guide";
    }

    /**
     * 上传包
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/app/upload")
    @ResponseBody
    public BaseResponse upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String token = request.getParameter("token");
            User user = this.userService.findByToken(token);
            if (user == null) {
                Subject currentUser = SecurityUtils.getSubject();
                user = (User) currentUser.getPrincipal();
            }
            // 无用户信息不允许上传
            if (user == null) {
                return ResponseUtil.unauthz();
            }
            // 检测文件
            String filePath = storageUtil.checkAndTransfer(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            if (filePath == null) {
                return ResponseUtil.fail(401, "不支持的文件类型");
            }
            // 获取扩展参数
            Map<String, String> extra = new HashMap<>();
            String jobName = request.getParameter("jobName");
            String buildNumber = request.getParameter("buildNumber");
            if (StringUtils.hasLength(jobName)) {
                extra.put("jobName", jobName);
            }
            if (StringUtils.hasLength(buildNumber)) {
                extra.put("buildNumber", buildNumber);
            }
            Package aPackage = this.packageService.save(filePath, extra, user);
//            App app = this.appService.savePackage(aPackage, user);
//            // URL
//            String codeURL = this.pathManager.getBaseURL(false) + "p/code/" + app.getCurrentPackage().getId();
//            // 发送WebHook消息
//            WebHookClient.sendMessage(app, pathManager);
            return ResponseUtil.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.badArgument();
        }
    }

    /**
     * 下载文件源文件(ipa 或 apk)
     *
     * @param id
     * @param response
     */
    @RequestMapping("/p/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable("id") String id, HttpServletResponse response) {
        try {
            Package aPackage = this.packageService.get(id);
            String key = aPackage.getSourceFile().getKey();
            Storage storage = storageService.findByKey(key);
            if (key == null) {
                return ResponseEntity.notFound().build();
            }
            if (key.contains("../")) {
                return ResponseEntity.badRequest().build();
            }
            String type = storage.getType();
            MediaType mediaType = MediaType.parseMediaType(type);

            org.springframework.core.io.Resource file = storageUtil.loadAsResource(key);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            // 文件名称转换
            String fileName = aPackage.getName() + "_" + aPackage.getVersion();
            String ext = "." + FilenameUtils.getExtension(aPackage.getFileName());
            String appName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + appName + ext + "\"").body(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取 manifest
     *
     * @param id
     * @param response
     */
    @RequestMapping("/m/{id}")
    public void getManifest(@PathVariable("id") String id, HttpServletRequest request,  HttpServletResponse response) {
        try {
            PackageViewModel viewModel = this.packageService.findById(id, request);
            if (viewModel != null && viewModel.isiOS()) {
                response.setContentType("application/force-download");
                response.setHeader("Content-Disposition", "attachment;fileName=manifest.plist");
                Writer writer = new OutputStreamWriter(response.getOutputStream());
                PlistGenerator.generate(viewModel, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包二维码
     *
     * @param id
     * @param response
     */
    @RequestMapping("/p/code/{id}")
    public void getQrCode(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            PackageViewModel viewModel = this.packageService.findById(id, request);
            if (viewModel != null) {
                response.setContentType("image/png");
                QRCodeUtil.encode(viewModel.getPreviewURL()).withSize(250, 250).writeTo(response.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除包
     *
     * @param id
     * @return
     */
    @RequiresPermissions("/p/delete")
    @RequestMapping("/p/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteById(@PathVariable("id") String id) {
        Map<String, Object> map = new HashMap<>();
        try {
            this.packageService.deleteById(id);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

}
