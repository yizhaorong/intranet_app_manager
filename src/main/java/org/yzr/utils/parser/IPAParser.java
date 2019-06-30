package org.yzr.utils.parser;

import org.apache.commons.io.FileUtils;
import org.yzr.model.Package;
import org.yzr.utils.PNGConverter;
import org.yzr.utils.PathManager;
import org.yzr.utils.ZipUtils;
import org.yzr.utils.ipa.Plist;
import org.yzr.utils.ipa.Provision;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class IPAParser implements PackageParser {
    @Override
    public Package parse(String filePath) {
        try {
            Package aPackage = new Package();
            // 解压 IPA 包
            String targetPath = ZipUtils.unzip(filePath);
            String appPath = appPath(targetPath);
            String infoPlistPath = appPath + File.separator + "Info.plist";
            File infoPlistFile  = new File(infoPlistPath);
            // Plist 文件获取失败
            if (!infoPlistFile.exists()) return null;
            // 获取 infoPlist
            Plist infoPlist = Plist.parseWithFile(infoPlistFile);
            File ipaFile = new File(filePath);
            long currentTimeMillis = System.currentTimeMillis();

            aPackage.setSize(ipaFile.length());
            aPackage.setName(infoPlist.stringValueForPath("CFBundleDisplayName"));
            if (aPackage.getName() == null) {
                aPackage.setName(infoPlist.stringValueForPath("CFBundleName"));
            }
            aPackage.setVersion(infoPlist.stringValueForPath("CFBundleShortVersionString"));
            aPackage.setBuildVersion(infoPlist.stringValueForPath("CFBundleVersion"));
            aPackage.setBundleID(infoPlist.stringValueForPath("CFBundleIdentifier"));
            aPackage.setMinVersion(infoPlist.stringValueForPath("MinimumOSVersion"));
            aPackage.setCreateTime(currentTimeMillis);
            aPackage.setPlatform("ios");

            // 获取应用图标
            String iconPath = appIcon(appPath, infoPlist.stringValueForKeyPath("CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconName"));
            String iconTempPath = PathManager.getTempIconPath(aPackage);
            PNGConverter.convert(iconPath, iconTempPath);

            // 解析 Provision
            Provision provision = new Provision(appPath);

            // 清除目录
            FileUtils.deleteDirectory(new File(targetPath));
            return aPackage;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*获取 APP 路径*/
    private static String appPath(String path) {
        try {
            String payloadPath = path + File.separator + "Payload";
            File payloadFile = new File(payloadPath);
            if (!payloadFile.exists()) return null;
            if (!payloadFile.isDirectory()) return null;
            File[] listFiles = payloadFile.listFiles();
            String appName = null;
            for (File file : listFiles) {
                if (file.getName().contains(".app")) {
                    appName = file.getName();
                    break;
                }
            }
            if (appName == null) return null;
            return payloadPath + File.separator + appName;
        } catch (Exception e) {

        }
        return null;
    }

    // 获取 APP 图标
    private static String appIcon(String appPath, String iconName) {
        List<String> iconNames = new ArrayList<>();
        File appFile = new File(appPath);
        File[] listFiles = appFile.listFiles();
        for (File file : listFiles) {
            String pattern = iconName + "[4,6]0x[4,6]0@[2,3]?x.png";
            boolean isMatch = Pattern.matches(pattern, file.getName());
            if (isMatch) {
                iconNames.add(file.getName());
            }
        }
        if (iconNames.size() > 0) {
            return appPath + File.separator + iconNames.get(iconNames.size() - 1);
        }
        return null;
    }
}
