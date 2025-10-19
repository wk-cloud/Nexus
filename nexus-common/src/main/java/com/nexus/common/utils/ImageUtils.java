package com.nexus.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;


/**
 *  图片工具类
 * @author wk
 * @date 2023/2/6
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "image")
public class ImageUtils {

    /**
     * 水印内容
     */
    private String waterMarkContent = "@往事随風的个人博客：https://wk-blog.vip";

    /**
     * 水印 X 坐标
     * */
    private int waterMarkX = 20;

    /**
     * 水印 Y 坐标
     */
    private int waterMarkY = 20;

    /**
     * 水印字体名称
     */
    private String waterMarkFontName = "微软雅黑";

    /**
     * 水印字体大小
     */
    private int waterMarkFontSize = 20;

    /**
     * 零
     */
    private Integer zero = 0;

    /**
     * 一千零二十四
     */
    private Integer oneZeroTwoFour = 1024;

    /**
     * 最小尺寸
     */
    private Integer minSize = 900;

    /**
     * 中等尺寸
     */
    private Integer mediumSize = 2047;

    /**
     * 大尺寸
     */
    private Integer largeSize = 3275;

    /**
     * 大精度
     */
    private Double largeAccuracy = 0.85;

    /**
     * 中等精度
     */
    private Double mediumAccuracy = 0.6;

    /**
     * 小精度
     */
    private Double minAccuracy = 0.44;

    /**
     * 默认精度
     */
    private Double defaultAccuracy = 0.4;

    /**
     * 图片压缩后的大小
     */
    private int compressSize = 200;

    /**
     * 开启图片压缩
     */
    private boolean openCompress = false;


    /**
     * 图片压缩
     *
     * @param destFileSize 指定压缩范围（单位kb）
     * @param inputStream  输入流
     * @return {@link byte[]}
     * @throws IOException ioexception
     */
    public byte[] compress(InputStream inputStream, long destFileSize) throws IOException {
        if (ObjectUtils.isNull(inputStream)) {
            throw new RuntimeException("源文件不存在");
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] compress = this.compress(bos.toByteArray(), destFileSize);
        return compress;
    }

    /**
     * 图片压缩
     *
     * @param destFileSize 指定压缩范围（单位kb）
     * @param imageBytes   图像字节数组
     * @return {@link byte[]}
     */
    public byte[] compress(byte[] imageBytes, long destFileSize) throws IOException {
        if (ArrayUtils.isEmpty(imageBytes) || imageBytes.length <= zero || imageBytes.length < destFileSize * oneZeroTwoFour) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double accuracy = getAccuracy(srcSize / oneZeroTwoFour);
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        while (imageBytes.length > destFileSize * oneZeroTwoFour) {
            bis = new ByteArrayInputStream(imageBytes);
            bos = new ByteArrayOutputStream(imageBytes.length);
            Thumbnails.of(bis)
                    .scale(accuracy)
                    .outputFormat("jpg")
                    .outputQuality(accuracy)
                    .toOutputStream(bos);
            imageBytes = bos.toByteArray();
        }
        if (ObjectUtils.isNotNull(bis)) {
            bis.close();
        }
        if (ObjectUtils.isNotNull(bos)) {
            bos.close();
        }
        log.info("图片原始大小：{}", srcSize / oneZeroTwoFour);
        log.info("压缩后的大小：{}", imageBytes.length / oneZeroTwoFour);
        return imageBytes;
    }

    /**
     * 图片压缩
     *
     * @param destPath     目标路径
     * @param destFileSize 指定压缩范围（单位kb）
     * @param imageBytes   图像字节
     * @throws IOException ioexception
     */
    public void compress(byte[] imageBytes, String destPath, long destFileSize) throws IOException {
        if (ArrayUtils.isEmpty(imageBytes) || imageBytes.length <= zero) {
            throw new RuntimeException("源文件不存在");
        }
        BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(destPath));
        if(imageBytes.length < destFileSize * oneZeroTwoFour){
            bs.write(imageBytes);
            bs.flush();
        }else {
            long srcSize = imageBytes.length;
            double accuracy = getAccuracy(srcSize / oneZeroTwoFour);
            ByteArrayInputStream bis = null;
            ByteArrayOutputStream bos = null;
            while (imageBytes.length > destFileSize * oneZeroTwoFour) {
                bis = new ByteArrayInputStream(imageBytes);
                bos = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(bis)
                        .scale(accuracy)
                        .outputFormat("jpg")
                        .outputQuality(accuracy)
                        .toOutputStream(bos);
                imageBytes = bos.toByteArray();
            }
            bs.write(imageBytes);
            bs.flush();
            log.info("图片原始大小：{}", srcSize / oneZeroTwoFour);
            log.info("压缩后的大小：{}", imageBytes.length / oneZeroTwoFour);
            if (ObjectUtils.isNotNull(bis)) {
                bis.close();
            }
            if (ObjectUtils.isNotNull(bos)) {
                bos.close();
            }
        }
        if (ObjectUtils.isNotNull(bs)) {
            bs.close();
        }
    }

    /**
     * 自动调节精度（经验数值）
     *
     * @param size 源图片大小
     * @return double 图片压缩质量比
     */
    private double getAccuracy(long size) {
        double accuracy;
        if (size < minSize) {
            accuracy = largeAccuracy;
        } else if (size < mediumSize) {
            accuracy = mediumAccuracy;
        } else if (size < largeSize) {
            accuracy = minAccuracy;
        } else {
            accuracy = defaultAccuracy;
        }
        return accuracy;
    }

    /**
     * 转换为base64
     *
     * @param imageStream 图像流
     * @param format      格式
     * @return {@link String}
     */
    public String toBase64(InputStream imageStream, String format) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 10];
        int len = 0;
        try {
            while ((len = imageStream.read(buf)) != -1) {
                bs.write(buf, 0, len);
            }
            byte[] bytes = bs.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            if (StringUtils.isNotBlank(format)) {
                return "data:Image/" + format + ";base64," + base64;
            }
            return base64;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("图片转换base64失败,失败原因：", e);
        } finally {
            if (ObjectUtils.isNotNull(bs)) {
                try {
                    bs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ObjectUtils.isNotNull(imageStream)) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * 添加水印
     *
     * @param imageStream 图像流
     * @param outPath     输出路径
     * @param formatName  格式名称
     */
    public void watermark(InputStream imageStream, String outPath, String formatName) {
        try {
            if (openCompress) {
                // 1. 图片压缩
                byte[] compress = compress(imageStream, compressSize);
                imageStream = new ByteArrayInputStream(compress);
            }
            // 2. 获取图片对象
            BufferedImage srcImage = ImageIO.read(imageStream);
            // 3. 获取图片宽度和高度
            int width = srcImage.getWidth();
            int height = srcImage.getHeight();
            // 4. 添加水印
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 创建画笔
            Graphics2D graphics = bufferedImage.createGraphics();
            // 绘制原始图片
            graphics.drawImage(srcImage, 0, 0, width, height, null);
            // 根据图片的背景设置水印颜色
            graphics.setColor(new Color(255, 255, 255, 128));
            // 设置字体，画笔字体样式为微软雅黑，加粗，文字大小为 60pt
            graphics.setFont(new Font(waterMarkFontName, Font.BOLD, waterMarkFontSize));
            // 设置水印的坐标
            int x = width - getWaterMarkLength(waterMarkContent, graphics) - waterMarkX;
            int y = height - waterMarkY;
            // 画出水印，第一个参数为水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
            graphics.drawString(waterMarkContent, x, y);
            graphics.dispose();
            // 输出图片
            FileOutputStream fos = new FileOutputStream(outPath);
            ImageIO.write(bufferedImage, formatName, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到水印长度
     *
     * @param waterMarkContent 水标内容
     * @param graphics2D       graphics2d
     * @return int
     */
    private int getWaterMarkLength(String waterMarkContent, Graphics2D graphics2D) {
        return graphics2D.getFontMetrics(graphics2D.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
}
