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

    /**
     * 预览页
     * @param code
     * @param request
     * @return
     */
    @GetMapping("/s/{code}")
    public String get(@PathVariable("code") String code, HttpServletRequest request) {
        String id = request.getParameter("id");
        AppViewModel viewModel = this.appService.findByCode(code, id);
        request.setAttribute("app", viewModel);
        request.setAttribute("ca_path", this.pathManager.getCAPath());
        return "install";
    }

    /**
     * 上传包
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/app/upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            String filePath = transfer(file);
            Package aPackage = this.packageService.buildPackage(filePath);
            App app = this.appService.getByPackage(aPackage);
            app.getPackageList().add(aPackage);
            app.setCurrentPackage(aPackage);
            aPackage.setApp(app);
            app = this.appService.save(app);
            // URL
            String codeURL = this.pathManager.getBaseURL(false) + "p/code/" + app.getCurrentPackage().getId();
            map.put("code", codeURL);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 下载文件源文件(ipa 或 apk)
     * @param id
     * @param response
     */
    @RequestMapping("/p/{id}")
    public void download(@PathVariable("id") String id, HttpServletResponse response) {
        try {
            Package aPackage = this.packageService.get(id);
            String path = PathManager.getFullPath(aPackage) + aPackage.getFileName();
            File file = new File(path);
            if(file.exists()){ //判断文件父目录是否存在
                response.setContentType("application/force-download");
                // 文件名称转换
                String fileName = aPackage.getName() + "_" + aPackage.getVersion();
                String ext =  "." + FilenameUtils.getExtension(aPackage.getFileName());
                String appName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + appName + ext);

                byte[] buffer = new byte[1024];
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
                bis.close();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 manifest
     * @param id
     * @param response
     */
    @RequestMapping("/m/{id}")
    public void getManifest(@PathVariable("id") String id, HttpServletResponse response) {
        try {
            PackageViewModel viewModel = this.packageService.findById(id);
            if (viewModel != null && viewModel.isIOS()) {
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
     * @param id
     * @param response
     */
    @RequestMapping("/p/code/{id}")
    public void getQrCode(@PathVariable("id") String id, HttpServletResponse response) {
        try {
            PackageViewModel viewModel = this.packageService.findById(id);
            if (viewModel != null) {
                response.setContentType("image/png");
                QRCode.from(viewModel.getPreviewURL()).withSize(250, 250).writeTo(response.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除包
     * @param id
     * @return
     */
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

    /**
     * 转存文件
     * @param srcFile
     * @return
     */
    private String transfer(MultipartFile srcFile) {
        try {
            // 获取文件后缀
            String fileName = srcFile.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            // 生成文件名
            String newFileName = UUID.randomUUID().toString() + "." + ext;
            // 转存到 tmp
            String destPath = FileUtils.getTempDirectoryPath() + File.separator + newFileName;
            srcFile.transferTo(new File(destPath));
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
