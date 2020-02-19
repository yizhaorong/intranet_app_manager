package org.yzr.utils.parser;

import com.dd.plist.NSDate;
import org.apache.commons.io.FileUtils;
import org.yzr.model.Package;
import org.yzr.model.Provision;
import org.yzr.utils.file.PathManager;
import org.yzr.utils.file.ZipUtil;
import org.yzr.utils.image.PNGConverter;
import org.yzr.utils.ipa.InfoPlist;
import org.yzr.utils.ipa.Plist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.List;
import java.util.regex.Pattern;

public class IPAParser implements PackageParser {
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
                if (file.getName().endsWith(".app")) {
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
            String pattern = iconName + "([4,6]0x[4,6]0)?(@[2,3]x)?(\\.png)?";
            boolean isMatch = Pattern.matches(pattern, file.getName());
            if (isMatch) {
                iconNames.add(file.getName());
            } else if (file.getName().equals(iconName)) {
                iconNames.add(file.getName());
            }
        }
        if (iconNames.size() > 0) {
            return appPath + File.separator + iconNames.get(iconNames.size() - 1);
        }
        return null;
    }

    private static Provision getProvision(String appPath) {
        Provision provision = new Provision();
        String profile = appPath + File.separator + "embedded.mobileprovision";
        File profileFile = new File(profile);
        // 文件不存在
        if (!profileFile.exists()) {
            provision.setType("Release");
            return provision;
        }

        try {
            boolean started = false;
            boolean ended = false;
            BufferedReader reader = new BufferedReader(new FileReader(profileFile));
            StringBuffer plist = new StringBuffer();
            String str = null;
            while ((str = reader.readLine()) != null) {
                if (str.contains("</plist>")) {
                    ended = true;
                    plist.append("</plist>").append("\n");
                } else if (started && !ended) {
                    plist.append(str).append("\n");
                } else if (str.contains("<?xml")) {
                    started = true;
                    plist.append(str.substring(str.indexOf("<?xml"))).append("\n");
                }
            }
            reader.close();
            Plist provisionFile = Plist.parseWithString(plist.toString());
            provision.setEnterprise(provisionFile.boolValueForPath("ProvisionsAllDevices"));
            List<String> provisionedDevices = provisionFile.arrayValueForPath("ProvisionedDevices");
            String[] devices = new String[provisionedDevices.size()];
            devices = provisionedDevices.toArray(devices);
            provision.setDevices(devices);
            provision.setDeviceCount(devices.length);
            provision.setTeamName(provisionFile.stringValueForPath("TeamName"));
            provision.setTeamID(provisionFile.arrayValueForPath("TeamIdentifier").get(0));
            long createDate =((NSDate) provisionFile.valueForKeyPath("CreationDate")).getDate().getTime();
            provision.setCreateDate(createDate);
            long expirationDate = ((NSDate) provisionFile.valueForKeyPath("ExpirationDate")).getDate().getTime();
            provision.setExpirationDate(expirationDate);
            provision.setUUID(provisionFile.stringValueForPath("UUID"));
            provision.setType(provision.getDeviceCount() > 0 ? "AdHoc" : "Release");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provision;
    }

    @Override
    public Package parse(String filePath) {
        try {
            Package aPackage = new Package();
            // 解压 IPA 包
            String targetPath = ZipUtil.unzip(filePath);
            String appPath = appPath(targetPath);
            String infoPlistPath = appPath + File.separator + "Info.plist";
            infoPlistPath = infoPlistPath.replaceAll("//", "/");
            File infoPlistFile = new File(infoPlistPath);
            // Plist 文件获取失败
            if (!infoPlistFile.exists()) return null;
            // 获取 infoPlist
            Plist plist = Plist.parseWithFile(infoPlistFile);
            InfoPlist  infoPlist = new InfoPlist(plist);
            File ipaFile = new File(filePath);

            aPackage.setSize(ipaFile.length());
            aPackage.setName(infoPlist.getAppName());
            aPackage.setVersion(infoPlist.getVersion());
            aPackage.setBuildVersion(infoPlist.getBuildVersion());
            aPackage.setBundleID(infoPlist.getBundleID());
            aPackage.setMinVersion(infoPlist.getMinimumOSVersion());
            aPackage.setCreateTime(System.currentTimeMillis());
            aPackage.setPlatform("ios");

            // 获取应用图标
            String iconName = infoPlist.getIconName();
            String iconPath = appIcon(appPath, iconName);
            String iconTempPath = PathManager.getTempIconPath(aPackage);
            PNGConverter.convert(iconPath, iconTempPath);

            // 解析 Provision
            aPackage.setProvision(getProvision(appPath));

            // 清除目录
            FileUtils.deleteDirectory(new File(targetPath));
            return aPackage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
