package org.yzr.utils.parser;

import org.yzr.model.Package;

public interface PackageParser {
    // 解析包
    public Package parse(String filePath);
}
