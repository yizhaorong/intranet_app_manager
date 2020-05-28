package org.yzr.utils.file;

import java.io.*;


public class FileUtil {
    /**
     * 判断文件类型
     */
    public static FileType getType(String filePath) {
        // 获取文件头
        String fileHead = getFileHeader(filePath);
        return getFileType(fileHead);
    }


    public static FileType getType(InputStream inputStream) {
        return getFileType(getFileHeader(inputStream));
    }

    private static FileType getFileType(String fileHead) {
        if (fileHead != null && fileHead.length() > 0) {
            fileHead = fileHead.toUpperCase();
            FileType[] fileTypes = FileType.values();
            for (FileType type : fileTypes) {
                if (fileHead.startsWith(type.getValue())) {
                    return type;
                }
            }
        }
        return null;
    }

    private static String getFileHeader(InputStream inputStream) {
        InputStream stream = copy(inputStream);
        byte[] b = new byte[28];
        try {
            stream.read(b, 0, 28);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHex(b);
    }

    private static InputStream copy(InputStream inputStream) {
        InputStream cloneInputStream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            cloneInputStream = new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneInputStream;
    }

    /**
     * 读取文件头
     */
    private static String getFileHeader(String filePath) {
        byte[] b = new byte[28];
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(filePath);
            inputStream.read(b, 0, 28);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesToHex(b);
    }

    /**
     * 将字节数组转换成16进制字符串
     */
    private static String bytesToHex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
