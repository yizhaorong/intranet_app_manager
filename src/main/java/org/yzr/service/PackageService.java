package org.yzr.service;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.yzr.dao.AppDao;
import org.yzr.dao.PackageDao;
import org.yzr.dao.UserDao;
import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.model.Storage;
import org.yzr.model.User;
import org.yzr.storage.StorageUtil;
import org.yzr.utils.CharUtil;
import org.yzr.utils.file.FileType;
import org.yzr.utils.file.PathManager;
import org.yzr.utils.image.ImageUtils;
import org.yzr.utils.parser.ParserClient;
import org.yzr.vo.PackageViewModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
public class PackageService {

    @Resource
    private PackageDao packageDao;
    @Resource
    private PathManager pathManager;
    @Resource
    private UserDao userDao;
    @Resource
    private AppDao appDao;
    @Resource
    private StorageUtil storageUtil;

    @Transactional
    public Package save(Package aPackage) {
        return this.packageDao.save(aPackage);
    }

    @Transactional
    public Package get(String id) {
        Package aPackage = this.packageDao.findById(id).get();
        // 级联查询用户
        aPackage.getApp().getOwner().getId();
        aPackage.getSourceFile().getKey();
        return aPackage;
    }

    @Transactional
    public PackageViewModel findById(String id, HttpServletRequest request) {
        Package aPackage = this.packageDao.findById(id).get();
        PackageViewModel viewModel = new PackageViewModel(aPackage, request);
        return viewModel;
    }

    @Transactional
    public void deleteById(String id) {
        Package aPackage = this.packageDao.findById(id).get();
        if (aPackage != null) {
            this.packageDao.deleteById(id);
            String path = PathManager.getFullPath(aPackage);
            PathManager.deleteDirectory(path);
        }

    }
    @Transactional
    public Package save(String filePath, Map<String, String> extra, User user) throws Exception {
        Package aPackage = ParserClient.parse(filePath);
        String fileName = aPackage.getPlatform() + "." + FilenameUtils.getExtension(filePath);
        // 更新文件名
        aPackage.setFileName(fileName);
        // 获取用户信息
        user = this.userDao.findById(user.getId()).get();
        // 设置用户信息
        App app = this.appDao.getByBundleIDAndPlatformAndOwner(aPackage.getBundleID(), aPackage.getPlatform(), user);
        if (app == null) {
            app = new App();
            String shortCode = CharUtil.generate(4);
            while (this.appDao.findByShortCode(shortCode) != null) {
                shortCode = CharUtil.generate(4);
            }
            BeanUtils.copyProperties(aPackage, app);
            app.setShortCode(shortCode);
            app.setOwner(user);
        } else {
            // 级联查询
            app.getPackageList().forEach(aPackage1 -> {
            });
            app.getWebHookList().forEach(webHook -> {});
        }
        aPackage.setApp(app);
        String packagePath = PathManager.getFullPath(aPackage);
        String tempIconPath = PathManager.getTempIconPath(aPackage);
        String sourcePath = packagePath + File.separator + fileName;
        // 获取文件后缀
        String ext = FilenameUtils.getExtension(fileName);
        // 生成文件名
        String newFileName = UUID.randomUUID().toString() + ".png";
        // 转存到 tmp
        String iconPath = FileUtils.getTempDirectoryPath() + File.separator + newFileName;
        iconPath = iconPath.replaceAll("//", "/");

        // 拷贝图标
        ImageUtils.resize(tempIconPath, iconPath, 192, 192);
        // 源文件
        FileUtils.copyFile(new File(filePath), new File(sourcePath));

        // 删除临时图标
        FileUtils.forceDelete(new File(tempIconPath));
        // 源文件
        FileUtils.forceDelete(new File(filePath));
        File sourceFile = new File(sourcePath);
        File iconFile = new File(iconPath);
        Storage storage = storageUtil.store(new FileInputStream(sourceFile), sourceFile.length(), "application/octet-stream", fileName);
        Storage iconStorage = storageUtil.store(new FileInputStream(iconFile), iconFile.length(), "application/x-png", iconFile.getName());
        // 删除临时图标
        FileUtils.forceDelete(sourceFile);
        // 源文件
        FileUtils.forceDelete(iconFile);
        aPackage.setIconFile(iconStorage);
        aPackage.setSourceFile(storage);
        aPackage = this.packageDao.save(aPackage);

        app.setName(aPackage.getName());
        app.getPackageList().add(aPackage);
        app.setCurrentPackage(aPackage);
        this.appDao.save(app);
        return aPackage;
    }
}
