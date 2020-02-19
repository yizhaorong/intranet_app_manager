package org.yzr.vo;

import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.utils.file.PathManager;

import java.util.ArrayList;
import java.util.List;


public class AppViewModel {

    private String id;

    private String name;

    private String platform;

    private String bundleID;

    private String icon;

    private String version;

    private String buildVersion;

    private String minVersion;

    private String shortCode;

    private String installPath;

    private String userId;

    private List<PackageViewModel> packageList;

    private PackageViewModel currentPackage;

    /***
     * 初始化是否加载列表
     * @param app
     * @param pathManager
     * @param loadList
     */
    public AppViewModel(App app, PathManager pathManager, boolean loadList) {
        this.id = app.getId();
        this.platform = app.getPlatform();
        this.bundleID = app.getBundleID();
        app.getCurrentPackage().setApp(app);
        this.userId = app.getOwner().getId();
        this.icon =  PathManager.getRelativePath(app.getCurrentPackage()) + "icon.png";
        Package aPackage = findPackageById(app, null);
        this.version = aPackage.getVersion();
        this.buildVersion = aPackage.getBuildVersion();
        this.shortCode = app.getShortCode();
        this.name = app.getName();
        this.installPath = pathManager.getBaseURL(false) + "s/" + app.getShortCode();
        this.minVersion = aPackage.getMinVersion();
        this.currentPackage = new PackageViewModel(aPackage, pathManager);
        if (loadList) {
            // 排序
            this.packageList = sortPackages(app.getPackageList(), pathManager);
        }
    }

    public AppViewModel(App app, PathManager pathManager, String packageId) {
        this.id = app.getId();
        this.platform = app.getPlatform();
        this.bundleID = app.getBundleID();
        this.icon =  PathManager.getRelativePath(app.getCurrentPackage()) + "icon.png";
        Package aPackage = findPackageById(app, packageId);
        this.version = aPackage.getVersion();
        this.buildVersion = aPackage.getBuildVersion();
        this.shortCode = app.getShortCode();
        this.name = app.getName();
        this.installPath = pathManager.getBaseURL(false) + "s/" + app.getShortCode();
        this.minVersion = aPackage.getMinVersion();
        this.currentPackage = new PackageViewModel(aPackage, pathManager);
    }

    private static Package findPackageById(App app, String id) {
        if (id != null) {
            for (Package aPackage : app.getPackageList()) {
                if (aPackage.getId().equals(id)) {
                    return aPackage;
                }
            }

        }
        return app.getCurrentPackage();
    }

    private static List<PackageViewModel> sortPackages(List<Package> packages, PathManager pathManager) {
        // 排序
        List<PackageViewModel> packageViewModels = new ArrayList<>();
        for (Package aPackage : packages) {
            PackageViewModel packageViewModel = new PackageViewModel(aPackage, pathManager);
            packageViewModels.add(packageViewModel);
        }
        packageViewModels.sort((o1, o2) -> {
            if (o1.getCreateTime() > o2.getCreateTime()) {
                return -1;
            }
            return 1;
        });
        return packageViewModels;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public String getBundleID() {
        return bundleID;
    }

    public String getIcon() {
        return icon;
    }

    public String getVersion() {
        return version;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getInstallPath() {
        return installPath;
    }

    public List<PackageViewModel> getPackageList() {
        return packageList;
    }

    public PackageViewModel getCurrentPackage() {
        return currentPackage;
    }

    public String getUserId() {
        return userId;
    }
}
