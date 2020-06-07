package org.yzr.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
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
     * @param contentType
     * @param fileName
     * @return
     */
    public String checkAndTransfer(InputStream inputStream, String contentType, String fileName) {
        try {
            // 获取文件后缀
            String ext = FilenameUtils.getExtension(fileName);
            // 生成文件名
            String newFileName = UUID.randomUUID().toString() + "." + ext;
            // 转存到 tmp
            String destPath = FileUtils.getTempDirectoryPath() + File.separator + newFileName;
            destPath = destPath.replaceAll("//", "/");
            Files.copy(inputStream, Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
            FileType type = FileUtil.getType(destPath);
            // ipa和apk文件都是zip文件
            if (type != FileType.ZIP) {
                // 删除文件
                FileUtils.forceDelete(new File(destPath));
                return null;
            }
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
        String key = generateKey(fileName);
        storage.store(inputStream, contentLength, contentType, key);

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
