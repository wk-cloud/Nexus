package com.nexus.common.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 图片工具配置
 *
 * @author wk
 * @date 2026/6/19 16:00
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "image")
public class ImageUtilsProperties {

    /**
     * 水印内容
     */
    private String waterMarkContent = "@默认水印";

    /**
     * 开启图片压缩
     */
    private Boolean openCompress = false;

    /**
     * 水印 X 坐标
     * */
    private Integer waterMarkX = 20;

    /**
     * 水印 Y 坐标
     */
    private Integer waterMarkY = 20;

    /**
     * 水印字体名称
     */
    private String waterMarkFontName = "微软雅黑";

    /**
     * 水印字体大小
     */
    private Integer waterMarkFontSize = 20;

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
    private Integer compressSize = 200;
}
