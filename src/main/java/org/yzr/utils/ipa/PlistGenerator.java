package org.yzr.utils.ipa;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.util.ResourceUtils;
import org.yzr.vo.PackageViewModel;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PlistGenerator {
    public static void generate(PackageViewModel aPackage, String destPath) {
        try {
            Writer out = new FileWriter(new File(destPath));
            generate(aPackage, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 manifest
     * @param aPackage
     * @param out
     */
    public static void generate(PackageViewModel aPackage, Writer out) {
        try {
            //1.0 创建配置对象
            //创建Configuration实例，指定FreeMarker的版本
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            //指定模板所在的目录
            cfg.setClassLoaderForTemplateLoading(PlistGenerator.class.getClassLoader(), "/freemarker");
            //设置默认字符集
            cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());

            //2.0 创建数据模型
            Map<String, Object> root = new HashMap<>();
            root.put("aPackage", aPackage);

            //3.0 获取模板
            Template template = cfg.getTemplate("manifest.plist");
            //4.0 给模板绑定数据模型
            template.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
