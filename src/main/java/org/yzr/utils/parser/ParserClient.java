package org.yzr.utils.parser;

import org.apache.commons.io.FilenameUtils;
import org.yzr.model.Package;

public class ParserClient {

    /**
     * 解析包
     * @param filePath 文件路径
     * @return
     */
    public static Package parse(String filePath) {
        PackageParser parser = getParser(filePath);
        if (parser != null) {
            return parser.parse(filePath);
        }
        return null;
    }

    /**
     * 根据文件后缀名获取解析器
     * @param filePath
     * @return
     */
    private static PackageParser getParser(String filePath) {
        String extension = FilenameUtils.getExtension(filePath);
        try {
            // 动态获取解析器
            Class aClass = Class.forName("org.yzr.utils.parser." + extension.toUpperCase()+"Parser");
            PackageParser packageParser = (PackageParser)aClass.newInstance();
            return packageParser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
