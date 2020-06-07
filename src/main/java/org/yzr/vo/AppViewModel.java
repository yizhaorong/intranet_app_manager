package org.yzr.vo;

import org.yzr.model.App;
import org.yzr.model.Package;
import org.yzr.utils.file.PathManager;

import javax.servlet.http.HttpServletRequest;
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
     * @param request
     * @param loadList
     */
    public AppViewModel(App app, HttpServletRequest request, boolean loadList) {
        String httpURL = PathManager.request(request).getBaseURL();
        this.id = app.getId();
        this.platform = app.getPlatform();
        this.bundleID = app.getBundleID();
        app.getCurrentPackage().setApp(app);
        this.userId = app.getOwner().getId();
        this.icon =  httpURL + "/fetch/" + app.getCurrentPackage().getIconFile().getKey();
        Package aPackage = findPackageById(app, null);
        this.version = aPackage.getVersion();
        this.buildVersion = aPackage.getBuildVersion();
        this.shortCode = app.getShortCode();
        this.name = app.getName();
        this.installPath = httpURL + "/s/" + app.getShortCode();
        this.minVersion = aPackage.getMinVersion();
        this.currentPackage = new PackageViewModel(aPackage, request);
        if (loadList) {
            // 排序
            this.packageList = sortPackages(app.getPackageList(), request);
        }
    }

    public AppViewModel(App app, HttpServletRequest request, String packageId) {
        String httpURL = PathManager.request(request).getBaseURL();
        this.id = app.getId();
        this.platform = app.getPlatform();
        this.bundleID = app.getBundleID();
        this.icon =  httpURL + "/fetch/" + app.getCurrentPackage().getIconFile().getKey();
        Package aPackage = findPackageById(app, packageId);
        this.version = aPackage.getVersion();
        this.buildVersion = aPackage.getBuildVersion();
        this.shortCode = app.getShortCode();
        this.name = app.getName();
        this.installPath = httpURL + "/s/" + app.getShortCode();
        this.minVersion = aPackage.getMinVersion();
        this.currentPackage = new PackageViewModel(aPackage, request);
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

    private static List<PackageViewModel> sortPackages(List<Package> packages, HttpServletRequest request) {
        // 排序
        List<PackageViewModel> packageViewModels = new ArrayList<>();
        for (Package aPackage : packages) {
            PackageViewModel packageViewModel = new PackageViewModel(aPackage, request);
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
