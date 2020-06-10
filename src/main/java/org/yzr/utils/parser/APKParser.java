package org.yzr.utils.parser;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.IconFace;
import org.apache.commons.io.FileUtils;
import org.yzr.model.Package;
import org.yzr.model.Storage;
import org.yzr.utils.file.PathManager;
import org.yzr.utils.image.PNGConverter;

import java.io.File;


public class APKParser implements PackageParser {
    @Override
    public Package parse(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) return null;
            ApkFile apkFile = new ApkFile(file);
            Package aPackage = new Package();
            aPackage.setSize(file.length());
            ApkMeta meta = apkFile.getApkMeta();
            aPackage.setName(meta.getName());
            String version = meta.getVersionName();
            String buildVersion = meta.getVersionCode() + "";
            if (version.length() < 1) {
                version = meta.getPlatformBuildVersionName();
            }
            if (buildVersion.length() < 1) {
                buildVersion = meta.getPlatformBuildVersionCode();
            }
            aPackage.setVersion(version);
            aPackage.setBuildVersion(buildVersion);
            aPackage.setBundleID(meta.getPackageName());
            aPackage.setMinVersion(meta.getMinSdkVersion());
            aPackage.setPlatform("android");
            aPackage.setCreateTime(System.currentTimeMillis());
            int iconCount = apkFile.getAllIcons().size();
            if (iconCount > 0) {
                IconFace icon = apkFile.getAllIcons().get(iconCount - 1);
                String iconPath = PathManager.getTempIconPath(aPackage);
                File iconFile = new File(iconPath);
                FileUtils.writeByteArrayToFile(iconFile, icon.getData());
                String sourceIconPath = PathManager.getTempFilePath("png");
                PNGConverter.convert(iconPath, sourceIconPath);
                Storage iconStorage = new Storage();
                iconStorage.setUrl(sourceIconPath);
                aPackage.setIconFile(iconStorage);
            }
            apkFile.close();
            return aPackage;
        } catch (Exception e) {

        }
        return null;
    }
}
