package com.nexus.common.core.utils;

import com.nexus.common.core.config.properties.ImageUtilsProperties;
import com.nexus.common.core.utils.ObjectUtils;
import com.nexus.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


/**
 *  图片工具类
 * @author wk
 * @date 2023/2/6
 */
@Slf4j
public class ImageUtils {

    private static final ImageUtilsProperties imageUtilsProperties = SpringUtils.getBean(ImageUtilsProperties.class);

    /**
     * 常数0
     */
    private static final Integer CONST_ZERO = 0;

    /**
     * 常数1024
     */
    private static final Integer CONST_ONE_ZERO_TWO_FOUR = 1024;

    /**
     * 水印内容
     */
    private static final String WATER_MARK_CONTENT;

    /**
     * 水印 X 坐标
     * */
    private static final Integer WATER_MARK_X;

    /**
     * 水印 Y 坐标
     */
    private static final Integer WATER_MARK_Y;

    /**
     * 水印字体名称
     */
    private static final String WATER_MARK_FONT_NAME;

    /**
     * 水印字体大小
     */
    private static final Integer WATER_MARK_FONT_SIZE;

    /**
     * 最小尺寸
     */
    private static final Integer MIN_SIZE;

    /**
     * 中等尺寸
     */
    private static final Integer MEDIUM_SIZE;

    /**
     * 大尺寸
     */
    private static final Integer LARGE_SIZE;

    /**
     * 大精度
     */
    private static final Double LARGE_ACCURACY;

    /**
     * 中等精度
     */
    private static final Double MEDIUM_ACCURACY;

    /**
     * 小精度
     */
    private static final Double MIN_ACCURACY;

    /**
     * 默认精度
     */
    private static final Double DEFAULT_ACCURACY;

    /**
     * 图片压缩后的大小
     */
    private static final Integer COMPRESS_SIZE;

    /**
     * 开启图片压缩
     */
    private static final Boolean OPEN_COMPRESS;

    static {
        WATER_MARK_CONTENT = imageUtilsProperties.getWaterMarkContent();
        WATER_MARK_X = imageUtilsProperties.getWaterMarkX();
        WATER_MARK_Y = imageUtilsProperties.getWaterMarkY();
        WATER_MARK_FONT_NAME = imageUtilsProperties.getWaterMarkFontName();
        WATER_MARK_FONT_SIZE = imageUtilsProperties.getWaterMarkFontSize();
        MIN_SIZE = imageUtilsProperties.getMinSize();
        MEDIUM_SIZE = imageUtilsProperties.getMediumSize();
        LARGE_SIZE = imageUtilsProperties.getLargeSize();
        LARGE_ACCURACY = imageUtilsProperties.getLargeAccuracy();
        MEDIUM_ACCURACY = imageUtilsProperties.getMediumAccuracy();
        MIN_ACCURACY = imageUtilsProperties.getMinAccuracy();
        DEFAULT_ACCURACY = imageUtilsProperties.getDefaultAccuracy();
        COMPRESS_SIZE = imageUtilsProperties.getCompressSize();
        OPEN_COMPRESS = imageUtilsProperties.getOpenCompress();
    }


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
        return this.compress(bos.toByteArray(), destFileSize);
    }

    /**
     * 图片压缩
     *
     * @param destFileSize 指定压缩范围（单位kb）
     * @param imageBytes   图像字节数组
     * @return {@link byte[]}
     */
    public byte[] compress(byte[] imageBytes, long destFileSize) throws IOException {
        if (ArrayUtils.isEmpty(imageBytes) || imageBytes.length <= CONST_ZERO || imageBytes.length < destFileSize * CONST_ONE_ZERO_TWO_FOUR) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double accuracy = getAccuracy(srcSize / CONST_ONE_ZERO_TWO_FOUR);
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        while (imageBytes.length > destFileSize * CONST_ONE_ZERO_TWO_FOUR) {
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
        log.info("图片原始大小：{}", srcSize / CONST_ONE_ZERO_TWO_FOUR);
        log.info("压缩后的大小：{}", imageBytes.length / CONST_ONE_ZERO_TWO_FOUR);
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
        if (ArrayUtils.isEmpty(imageBytes) || imageBytes.length <= CONST_ZERO) {
            throw new RuntimeException("源文件不存在");
        }
        BufferedOutputStream bs = new BufferedOutputStream(Files.newOutputStream(Paths.get(destPath)));
        if(imageBytes.length < destFileSize * CONST_ONE_ZERO_TWO_FOUR){
            bs.write(imageBytes);
            bs.flush();
        }else {
            long srcSize = imageBytes.length;
            double accuracy = getAccuracy(srcSize / CONST_ONE_ZERO_TWO_FOUR);
            ByteArrayInputStream bis = null;
            ByteArrayOutputStream bos = null;
            while (imageBytes.length > destFileSize * CONST_ONE_ZERO_TWO_FOUR) {
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
            log.info("图片原始大小：{}", srcSize / CONST_ONE_ZERO_TWO_FOUR);
            log.info("压缩后的大小：{}", imageBytes.length / CONST_ONE_ZERO_TWO_FOUR);
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
        if (size < MIN_SIZE) {
            accuracy = LARGE_ACCURACY;
        } else if (size < MEDIUM_SIZE) {
            accuracy = MEDIUM_ACCURACY;
        } else if (size < LARGE_SIZE) {
            accuracy = MIN_ACCURACY;
        } else {
            accuracy = DEFAULT_ACCURACY;
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
            throw new RuntimeException("图片转换base64失败,失败原因：", e);
        } finally {
            if (ObjectUtils.isNotNull(bs)) {
                try {
                    bs.close();
                } catch (IOException e) {
                    log.error("关闭流失败", e);
                }
            }
            if (ObjectUtils.isNotNull(imageStream)) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    log.error("关闭流失败", e);
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
            if (OPEN_COMPRESS) {
                // 1. 图片压缩
                byte[] compress = compress(imageStream, COMPRESS_SIZE);
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
            graphics.setFont(new Font(WATER_MARK_FONT_NAME, Font.BOLD, WATER_MARK_FONT_SIZE));
            // 设置水印的坐标
            int x = width - getWaterMarkLength(WATER_MARK_CONTENT, graphics) - WATER_MARK_X;
            int y = height - WATER_MARK_Y;
            // 画出水印，第一个参数为水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
            graphics.drawString(WATER_MARK_CONTENT, x, y);
            graphics.dispose();
            // 输出图片
            FileOutputStream fos = new FileOutputStream(outPath);
            ImageIO.write(bufferedImage, formatName, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            log.error("水印添加失败", e);
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
