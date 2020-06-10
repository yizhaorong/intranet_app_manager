package org.yzr.utils.file;

import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.yzr.model.App;
import org.yzr.model.Package;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.InetAddress;
import java.util.UUID;

@Component
public class PathManager {

    @Resource
    private Environment environment;
    private String httpsBaseURL;
    private String httpBaseURL;
    private String host;
    private String scheme = "http";

    public static PathManager request(HttpServletRequest request) {
        PathManager pathManager = new PathManager();
        pathManager.host = request.getHeader("host");
        return pathManager;
    }

    public PathManager useHttps() {
        this.scheme = "https";
        return this;
    }

    public String getBaseURL() {
        return this.scheme + "://" + this.host;
    }

    /**
     * 获取图标的临时路径
     *
     * @param aPackage
     * @return
     */
    public static String getTempIconPath(Package aPackage) {
        if (aPackage == null) return null;
        StringBuilder path = new StringBuilder();
        path.append(FileUtils.getTempDirectoryPath()).append(File.separator).append(aPackage.getPlatform());
        path.append(File.separator).append(aPackage.getBundleID());
        // 如果目录不存在，创建目录
        File dir = new File(path.toString());
        if (!dir.exists()) dir.mkdirs();
        path.append(File.separator).append(aPackage.getCreateTime()).append(".png");
        return path.toString();
    }

    /**
     * 获取临时文件路径
     * @param ext 扩展名
     * @return
     */
    public static String getTempFilePath(String ext) {
        StringBuilder path = new StringBuilder();
        path.append(FileUtils.getTempDirectoryPath());
        // 如果目录不存在，创建目录
        File dir = new File(path.toString());
        if (!dir.exists()) dir.mkdirs();
        path.append(UUID.randomUUID().toString());
        path.append(".").append(ext);
        return path.toString();
    }

    /**
     * 获取上传路径
     *
     * @return
     */
    public static String getUploadPath() {
        try {
            //获取跟目录
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if (!path.exists()) path = new File("");

            //如果上传目录为/static/upload/，则可以如下获取：
            File upload = new File(path.getAbsolutePath(), "static/upload/");
            if (!upload.exists()) upload.mkdirs();
            return upload.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 APP 路径
     *
     * @param app
     * @return
     */
    public static String getAppPath(App app) {
        return getUploadPath() + File.separator + app.getPlatform() + File.separator + app.getBundleID() + File.separator;
    }

    /**
     * 获取包的完整路径
     *
     * @param aPackage
     * @return
     */
    public static String getFullPath(Package aPackage) {
        return getUploadPath() + File.separator + getRelativePath(aPackage);
    }

    /**
     * 获取包的相对路径
     *
     * @param aPackage
     * @return
     */
    public static String getRelativePath(Package aPackage) {
        if (aPackage == null) return null;
        StringBuilder path = new StringBuilder();
        path.append(aPackage.getPlatform()).append(File.separator);
        path.append(aPackage.getApp().getOwner().getId()).append(File.separator);
        path.append(aPackage.getBundleID()).append(File.separator);
        path.append(aPackage.getCreateTime()).append(File.separator);
        return path.toString();
    }

    /**
     * 清除目录
     *
     * @param path
     */
    public static void deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取包所在路径
     *
     * @param aPackage
     * @param isHttps
     * @return
     */
    public String getPackageResourceURL(Package aPackage, boolean isHttps) {
//        getBaseURL() + "/fetch/" + aPackage.getSourceFile().getKey();
//        String baseURL = getBaseURL(isHttps);
//        String resourceURL = baseURL + aPackage.getPlatform() + "/" + aPackage.getBundleID()
//                + "/" + aPackage.getCreateTime() + "/";
//        return resourceURL;
        return null;
    }

    /**
     * 获取证书路径
     *
     * @return
     */
    public String getCAPath() {
        return getBaseURL() + "/crt/ca.crt";
    }
}
