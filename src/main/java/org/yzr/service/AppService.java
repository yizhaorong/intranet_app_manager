package org.yzr.service;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.yzr.dao.AppDao;
import org.yzr.dao.PackageDao;
import org.yzr.dao.UserDao;
import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.model.User;
import org.yzr.utils.CharUtil;
import org.yzr.utils.file.PathManager;
import org.yzr.vo.AppViewModel;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {
    @Resource
    private AppDao appDao;
    @Resource
    private UserDao userDao;
    @Resource
    private PackageDao packageDao;
    @Resource
    private PathManager pathManager;

    @Transactional
    public App save(App app, User user) {
        user = this.userDao.findById(user.getId()).get();
        app.setOwner(user);
        app = this.appDao.save(app);
        app.getCurrentPackage();
        try {
            // 触发级联查询
            app.getWebHookList().forEach(webHook -> {
            });
        } catch (Exception e) {
        }
        return app;
    }

    @Transactional
    public List<AppViewModel> findAll() {
        Iterable<App> apps = this.appDao.findAll();
        List<AppViewModel> list = new ArrayList<>();
        for (App app : apps) {
            AppViewModel appViewModel = new AppViewModel(app, this.pathManager, false);
            list.add(appViewModel);
        }
        return list;
    }

    @Transactional
    public AppViewModel getById(String appID) {
        Optional<App> optionalApp = this.appDao.findById(appID);
        App app = optionalApp.get();
        if (app != null) {
            app.getPackageList().forEach(aPackage -> {
            });
            AppViewModel appViewModel = new AppViewModel(app, this.pathManager, true);
            return appViewModel;
        }
        return null;
    }

    @Transactional
    public App savePackage(Package aPackage, User user) {
        aPackage = this.packageDao.save(aPackage);
        user = this.userDao.findByUsername(user.getUsername());
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
        app.setName(aPackage.getName());
        app.getPackageList().add(aPackage);
        app.setCurrentPackage(aPackage);
        app = this.appDao.save(app);
        return app;
    }

    @Transactional
    public void deleteById(String id) {
        App app = this.appDao.findById(id).get();
        if (app != null) {
            this.appDao.deleteById(id);
            // 消除整个 APP 目录
            String path = PathManager.getAppPath(app);
            PathManager.deleteDirectory(path);
        }
    }

    /**
     * 通过 code 和 packageId 查询
     *
     * @param code
     * @param packageId
     * @return
     */
    @Transactional
    public AppViewModel findByCode(String code, String packageId) {
        App app = this.appDao.findByShortCode(code);
        AppViewModel viewModel = new AppViewModel(app, pathManager, packageId);
        return viewModel;
    }

    @Transactional
    public List<AppViewModel> findByUser(User user) {
        Iterable<App> apps = this.appDao.findByUser(user);
        List<AppViewModel> list = new ArrayList<>();
        for (App app : apps) {
            AppViewModel appViewModel = new AppViewModel(app, this.pathManager, false);
            list.add(appViewModel);
        }
        return list;
    }
}
