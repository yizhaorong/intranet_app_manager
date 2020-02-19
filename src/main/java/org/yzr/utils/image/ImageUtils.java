package org.yzr.utils.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;

public class ImageUtils {

    /**
     * 将图片转换为 Base64
     * @param filePath
     * @return
     */
    public static String convertImageToBase64(String filePath) {
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        return Base64Utils.encodeToString(data);
    }

    /**
     * 重置图片大小
     * @param soureFilePath
     * @param targetFilePath
     * @param height
     * @param width
     */
    public static void resize(String soureFilePath, String targetFilePath, int width, int height) {
        try {
            File input = new File(soureFilePath);
            File output = new File(targetFilePath);
            output.mkdirs();
            BufferedImage image = ImageIO.read(input);
            Image tmp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            ImageIO.write(resized, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将 PNG 转为 JPG 并指定图片大小
     * @param soureFilePath
     * @param targetFilePath
     * @param width
     * @param height
     */
    public static void convertPNGToJPG(String soureFilePath, String targetFilePath, int width, int height) {
        try {
            //read image file
            BufferedImage bufferedImage = ImageIO.read(new File(soureFilePath));
            Image tmp = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            // create a blank, RGB, same width and height, and a white background
            if (width <= 0 || height <= 0) {
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            }
            BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位

            newBufferedImage.createGraphics().drawImage(tmp, 0, 0, Color.WHITE, null);

            // write to jpeg file
            ImageIO.write(newBufferedImage, "jpg", new File(targetFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
