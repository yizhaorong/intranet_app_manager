package org.yzr.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtil {

    private String text;

    private Integer width = 200;

    private Integer height = 200;

    private static final float PERCENT = 0.2F;

    private static final int CORNER_RADIUS = 5;

    private File icon;

    private String format = "png";

    public static QRCodeUtil encode(String text) {
        QRCodeUtil util = new QRCodeUtil();
        util.text = text;
        return util;
    }

    public QRCodeUtil withSize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public QRCodeUtil withIcon(File icon) {
        this.icon = icon;
        return this;
    }

    public boolean writeTo(OutputStream outputStream) {
        try {
            BitMatrix bitMatrix = getBitMatrix();
            if (this.icon != null) {
                MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
                BufferedImage bufferedImage = drawIconInMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig), this.icon);
                ImageIO.write(bufferedImage, format, outputStream);
            } else {
                MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);
            }
            return true;
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeTo(File file) {
        try {
            BitMatrix bitMatrix = getBitMatrix();
            if (this.icon != null) {
                MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
                BufferedImage bufferedImage = drawIconInMatrix(MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig), this.icon);
                ImageIO.write(bufferedImage, format, file);
            } else {
                MatrixToImageWriter.writeToPath(bitMatrix, format, file.toPath());
            }
            return true;
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private BitMatrix getBitMatrix() throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置二维码边的空度，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        return new MultiFormatWriter().encode(this.text, BarcodeFormat.QR_CODE, width, height, hints);
    }


    /**
     * 识别指定文件的二维码
     * @param file
     * @return
     */
    public static String parseCode(File file)  {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseCode(bufferedImage);
    }

    /**
     * 识别指定图片的二维码
     * @param bufferedImage
     * @return
     */
    public static String parseCode(BufferedImage bufferedImage)  {
        String content = null;
        try {
            //读取指定的二维码文件
            MultiFormatReader formatReader = new MultiFormatReader();
            BinaryBitmap binaryBitmap= new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            //定义二维码参数
            Map hints= new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            Result result = formatReader.decode(binaryBitmap, hints);
            //输出相关的二维码信息
            System.out.println("解析结果："+result.toString());
            System.out.println("二维码格式类型："+result.getBarcodeFormat());
            System.out.println("二维码文本内容："+result.getText());
            content = result.getText();
            if (bufferedImage != null) {
                bufferedImage.flush();
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 识别输入流的二维码
     * @param inputStream
     * @return
     */
    public static String parseCode(InputStream inputStream) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseCode(bufferedImage);
    }

    /**
     * 二维码添加logo
     * @param matrixImage 源二维码图片
     * @param logoFile logo图片
     * @return 返回带有logo的二维码图片
     */
    private BufferedImage drawIconInMatrix(BufferedImage matrixImage, File logoFile) throws IOException{
        /**
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();
        int matrixWidth = matrixImage.getWidth();
        int matrixHeight = matrixImage.getHeight();

        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(logoFile);

        //开始绘制图片
        g2.drawImage(logo,(int)(matrixWidth * PERCENT*2),(int)(matrixHeight * PERCENT*2), (int)(matrixWidth * PERCENT), (int)(matrixHeight * PERCENT), null);//绘制
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth * PERCENT*2, matrixHeight * PERCENT*2, matrixWidth * PERCENT, matrixHeight * PERCENT,CORNER_RADIUS,CORNER_RADIUS);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth * PERCENT*2+2, matrixHeight * PERCENT*2+2, matrixWidth * PERCENT-4, matrixHeight * PERCENT-4,CORNER_RADIUS,CORNER_RADIUS);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush() ;
        return matrixImage ;
    }
}

