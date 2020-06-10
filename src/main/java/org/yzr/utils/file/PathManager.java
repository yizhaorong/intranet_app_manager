package org.yzr.utils.file;

import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

public class PathManager {
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
     * 获取证书路径
     *
     * @return
     */
    public String getCAPath() {
        return getBaseURL() + "/crt/ca.crt";
    }
}
