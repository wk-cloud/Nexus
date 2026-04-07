package com.nexus.common.core.utils;


/**
 * 系统工具类
 *
 * @author wk
 * @date 2024/01/04
 */
public class SystemUtils extends org.apache.commons.lang3.SystemUtils {

    private static final String WINDOWS = "Windows";

    private static final String LINUX = "Linux";

    private static final String MAC = "Mac";

    private SystemUtils(){}

    /**
     * 判断是否是 Windows 操作系统
     *
     * @return boolean
     */
    public static boolean isWindows(){
        String osName = getOsName();
        return StringUtils.isNotBlank(osName) && osName.startsWith(WINDOWS);
    }

    /**
     * 判断是否 是 MacOS 操作系统
     *
     * @return boolean
     */
    public static boolean isMacOs() {
        String osName = getOsName();
        return StringUtils.isNotBlank(osName) && osName.startsWith(MAC);
    }

    /**
     * 判断是否 是 Linux 操作系统
     *
     * @return boolean
     */
    public static boolean isLinux(){
        String osName = getOsName();
        return StringUtils.isNotBlank(osName) && osName.startsWith(LINUX);
    }

    /**
     * 获取操作系统名称
     *
     * @return {@link String}
     */
    public static String getOsName(){
        return System.getProperty("os.name");
    }

    /**
     * 获取系统环境变量
     *
     * @param envName 变量名
     * @return {@link String }
     */
    public static String getEnv(String envName) {
        return System.getenv(envName);
    }
}
