package org.yzr.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.yzr.model.Storage;
import org.yzr.utils.CharUtil;
import org.yzr.utils.file.FileType;
import org.yzr.utils.file.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 提供存储服务类，所有存储服务均由该类对外提供
 */
public class StorageUtil {
    private String active;
    private IStorage storage;
    @Autowired
    private org.yzr.service.StorageService storageService;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public IStorage getStorage() {
        return storage;
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }


    /**
     * 检测并转存文件
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @param fileName
     * @return
     */
    public static String checkAndTransfer(InputStream inputStream, long contentLength, String contentType, String fileName) {
        // 判断文件类型
        if (!(contentType != null && contentType.equalsIgnoreCase("application/octet-stream"))) {
            return null;
        }
        int len = 28;
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, len);
        try {
            byte[] b = new byte[len];
            FileType type = FileUtil.getType(pushbackInputStream);
            // ipa和apk文件都是zip文件
            if (type != FileType.ZIP) {
                pushbackInputStream.close();
                return null;
            }
            pushbackInputStream.unread(b);
            // 获取文件后缀
            String ext = FilenameUtils.getExtension(fileName);
            // 生成文件名
            String newFileName = UUID.randomUUID().toString() + "." + ext;
            // 转存到 tmp
            String destPath = FileUtils.getTempDirectoryPath() + File.separator + newFileName;
            destPath = destPath.replaceAll("//", "/");
            System.out.println(destPath);
            Files.copy(pushbackInputStream, Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param fileName      文件索引名
     */
    public Storage store(InputStream inputStream, long contentLength, String contentType, String fileName) {
        // 判断文件类型
        if (!(contentType != null && contentType.equalsIgnoreCase("application/octet-stream"))) {
            return null;
        }
        String key = generateKey(fileName);
        int len = 28;
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, len);
        try {
            byte[] b = new byte[len];
            FileType type = FileUtil.getType(pushbackInputStream);
            // ipa和apk文件都是zip文件
            if (type != FileType.ZIP) {
                pushbackInputStream.close();
                return null;
            }
            pushbackInputStream.unread(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        storage.store(pushbackInputStream, contentLength, contentType, key);

        String url = generateUrl(key);
        Storage storageInfo = new Storage();
        storageInfo.setName(fileName);
        storageInfo.setSize((int) contentLength);
        storageInfo.setType(contentType);
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        this.storageService.save(storageInfo);

        return storageInfo;
    }

    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        Storage storageInfo = null;

        do {
            key = CharUtil.generate(20) + suffix;
            storageInfo = this.storageService.findByKey(key);
        }
        while (storageInfo != null);

        return key;
    }

    public Stream<Path> loadAll() {
        return storage.loadAll();
    }

    public Path load(String keyName) {
        return storage.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        return storage.loadAsResource(keyName);
    }

    public void delete(String keyName) {
        storage.delete(keyName);
    }

    private String generateUrl(String keyName) {
        return storage.generateUrl(keyName);
    }
}
