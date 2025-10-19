package com.nexus.common.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * 文件工具类
 *
 * @author wk
 * @date 2024/01/03
 */
public class FileUtils extends FileUtil {

    /**
     * 资源模式解析器
     */
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * 文件分隔符
     */
    private static final String FILE_SEPARATOR = "/";

    /**
     * 从 URL 中提取文件名
     *
     * @param url 网址
     * @return {@link String }
     */
    public static String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * 获取 resources 目录下的指定文件后缀的所有文件名
     * jar 包中可用
     * @param directoryPath 目录路径，示例：classpath*:/xxx 或者 /xxx(如果不加 classpath*: ，默认会自动拼接上 classpath*: )
     * @param suffix        文件后缀
     * @return {@link List }<{@link File }>
     * @throws IOException 异常
     */
    public static List<String> getResourceFileNames(String directoryPath, String suffix) throws IOException {
        // 递归匹配目录下所有指定后缀文件
        String pattern = directoryPath + "/**/*." + suffix;
        if (!pattern.startsWith("classpath*:")) {
            pattern = "classpath*:" + pattern;
        }
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        List<String> contents = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.isReadable() && !isDirectory(resource)) {
                contents.add(resource.getFilename());
            }
        }
        return contents;
    }

    /**
     * 获取 resources 目录下的指定文件后缀的所有文件字节数组
     * jar 包中可用
     * @param directoryPath 目录路径，示例：classpath*:/xxx 或者 /xxx(如果不加 classpath*: ，默认会自动拼接上 classpath*: )
     * @param suffix        文件后缀
     * @return {@link List }<{@link byte[] }>
     * @throws IOException IOException
     */
    public static List<byte[]> getResourceFileBytes(String directoryPath, String suffix) throws IOException {
        // 递归匹配目录下所有指定后缀文件
        String pattern = directoryPath + "/**/*." + suffix;
        if (!pattern.startsWith("classpath*:")) {
            pattern = "classpath*:" + pattern;
        }
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        List<byte[]> contents = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.isReadable() && !isDirectory(resource)) {
                try (InputStream is = resource.getInputStream()) {
                    contents.add(FileStreamUtils.copyToByteArray(is));
                }
            }
        }
        return contents;
    }

    /**
     * 获取 resources 目录下的指定文件后缀的所有文件输入流
     * jar 包中可用
     * @param directoryPath 目录路径，示例：classpath*:/xxx 或者 /xxx(如果不加 classpath*: ，默认会自动拼接上 classpath*: )
     * @param suffix        文件后缀
     * @return {@link List }<{@link InputStream }>
     * @throws IOException IOException
     */
    public static List<InputStream> getResourceFileInputStreams(String directoryPath, String suffix) throws IOException {
        // 递归匹配目录下所有指定后缀文件
        String pattern = directoryPath + "/**/*." + suffix;
        if (!pattern.startsWith("classpath*:")) {
            pattern = "classpath*:" + pattern;
        }
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        List<InputStream> contents = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.isReadable() && !isDirectory(resource)) {
                try (InputStream is = resource.getInputStream()) {
                    contents.add(is);
                }
            }
        }
        return contents;
    }

    /**
     * 判断资源是否为目录（JAR包内目录表现为以'/'结尾）
     *
     * @param resource 资源
     * @return boolean
     */
    private static boolean isDirectory(Resource resource) {
        try {
            return resource.getURL().toString().endsWith("/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 resources 目录下的文件名称
     * 只能读取单个文件
     * jar包中不可用
     * @param path 文件路径，示例：classpath:xxx.txt
     * @return {@link String}
     */
    public static String getResourceFileName(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        if(classPathResource.exists()){
            return classPathResource.getFilename();
        }
        return null;
    }

    /**
     * 获取 resources 目录下的文件输入流
     * 只能读取单个文件
     * jar包中不可用
     * @param path 文件路径，示例：xxx/xxx.txt
     * @return {@link InputStream}
     */
    public static InputStream getResourceFileInputStream(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        if(classPathResource.exists()){
            return classPathResource.getInputStream();
        }
        return null;
    }

    /**
     * 在项目根目录创建指定目录
     *
     * @param directoryName 目录名称
     * @return {@link String}
     */
    public static String createRootDirectory(String directoryName) {
        String filePath = getRootDirectoryPath(directoryName);
        if (!exist(filePath)) {
            mkdir(filePath);
        }
        return filePath;
    }

    /**
     * 获取 target 根目录路径
     *
     * @param subPath 子级路径
     * @return {@link String}
     */
    public static String getTargetRootDirectoryPath(String subPath) {
        try {
            String rootPath = ResourceUtils.getURL("classpath:").getPath();
            if (StringUtils.isBlank(subPath)) {
                return rootPath;
            }
            if (subPath.contains(FILE_SEPARATOR) && SystemUtils.isWindows()) {
                subPath = toWindowsFileSeparator(subPath);
            }

            if (!subPath.startsWith(File.separator)) {
                subPath = File.separator.concat(subPath);
            }
            // 构建要写入的文件路径
            return rootPath + subPath;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取项目根目录路径
     *
     * @param subPath 子级路径
     * @return {@link String}
     */
    public static String getRootDirectoryPath(String subPath) {
        // 获取当前项目根路径
        String rootPath = System.getProperty("user.dir");

        if (StringUtils.isBlank(subPath)) {
            return rootPath;
        }

        if (subPath.contains(FILE_SEPARATOR) && SystemUtils.isWindows()) {
            subPath = toWindowsFileSeparator(subPath);
        }

        if (!subPath.startsWith(File.separator)) {
            subPath = File.separator.concat(subPath);
        }
        // 构建要写入的文件路径
        return rootPath + subPath;
    }

    /**
     * 创建日期层级目录 (返回以年月日命名的目录)
     * 格式：2024/01/13
     *
     * @param basePath 基本路径
     * @return {@link String}
     */
    public static String createDateLevelDirectory(String basePath) {
        String directoryPath = getDateLevelDirectoryPath(basePath);
        if (!exist(directoryPath)) {
            mkdir(directoryPath);
        }
        return directoryPath;
    }

    /**
     * 获取日期层级目录路径
     * 格式：2024/01/13
     *
     * @param basePath 基本路径
     * @return {@link String}
     */
    public static String getDateLevelDirectoryPath(String basePath) {
        if (StringUtils.isBlank(basePath)) {
            if (SystemUtils.isWindows()) {
                basePath = "C:\\";
            } else if (SystemUtils.isLinux()) {
                basePath = "/home/";
            } else if (SystemUtils.isMacOs()) {
                basePath = "/Users/";
            }
        }

        if (basePath.contains(FILE_SEPARATOR) && SystemUtils.isWindows()) {
            basePath = toWindowsFileSeparator(basePath);
        }

        if (!basePath.endsWith(File.separator)) {
            basePath = basePath.concat(File.separator);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String format = simpleDateFormat.format(new Date());

        int indexOf = format.indexOf("-");

        int lastIndexOf = format.lastIndexOf("-");

        String year = format.substring(0, indexOf);

        String month = format.substring(indexOf + 1, lastIndexOf);

        String day = format.substring(lastIndexOf + 1);

        return basePath + year + File.separator + month + File.separator + day + File.separator;
    }

    /**
     * 创建日期目录
     * 格式：/20240113
     *
     * @param basePath 基本路径
     * @return {@link String}
     */
    public static String createDateDirectory(String basePath) {
        String directoryPath = getDateDirectoryPath(basePath);
        if (!exist(directoryPath)) {
            mkdir(directoryPath);
        }
        return directoryPath;
    }

    /**
     * 获取日期目录路径
     * 格式：/20240113
     *
     * @param basePath 基本路径
     * @return {@link String}
     */
    public static String getDateDirectoryPath(String basePath) {
        if (StringUtils.isBlank(basePath)) {
            if (SystemUtils.isWindows()) {
                basePath = "C:\\";
            } else if (SystemUtils.isLinux()) {
                basePath = "/home/";
            } else if (SystemUtils.isMacOs()) {
                basePath = "/Users/";
            }
        }

        if (basePath.contains(FILE_SEPARATOR) && SystemUtils.isWindows()) {
            basePath = toWindowsFileSeparator(basePath);
        }

        if (!basePath.endsWith(File.separator)) {
            basePath = basePath.concat(File.separator);
        }

        String date = DateUtils.getYearMonthDay(new Date());

        return basePath + date + File.separator;
    }

    /**
     * 随机文件名
     *
     * @param file 文件
     * @return {@link String}
     */
    public static String randomFileName(MultipartFile file) {
        if (ObjectUtils.isNull(file)) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        String suffix = getCompleteSuffix(fileName);
        return UUID.randomUUID().toString().replace("-", "").concat(suffix);
    }

    /**
     * 随机文件名
     *
     * @param fileName 文件名
     * @return {@link String}
     */
    public static String randomFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        String suffix = getCompleteSuffix(fileName);
        return UUID.randomUUID().toString().replace("-", "").concat(suffix);
    }

    /**
     * 获取完整后缀 (后缀带点)
     *
     * @param file 文件
     * @return {@link String}
     */
    public static String getCompleteSuffix(MultipartFile file) {
        if (ObjectUtils.isNull(file)) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取完整后缀 (后缀带点)
     *
     * @param fileName 文件名
     * @return {@link String}
     */
    public static String getCompleteSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 转换为 Windows 文件分隔符
     *
     * @param path 文件路径
     * @return {@link String}
     */
    public static String toWindowsFileSeparator(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return path.replaceAll("/", "\\\\");
    }

    /**
     * 文件大小转换为 TB
     *
     * @param fileSize 文件大小
     * @return {@link Double}
     */
    public static Double toTb(Long fileSize) {
        return toGb(fileSize) / 1024;
    }

    /**
     * 文件大小转换为 GB
     *
     * @param fileSize 文件大小
     * @return {@link Double}
     */
    public static Double toGb(Long fileSize) {
        return toMb(fileSize) / 1024;
    }

    /**
     * 文件大小转换为 MB
     *
     * @param fileSize 文件大小
     * @return {@link Double}
     */
    public static Double toMb(Long fileSize) {
        return toKb(fileSize) / 1024;
    }

    /**
     * 文件大小转换为 KB
     *
     * @param fileSize 文件大小
     * @return {@link Long}
     */
    public static Double toKb(Long fileSize) {
        if (ObjectUtils.isNull(fileSize)) {
            return 0.0;
        }
        double size = Double.parseDouble(Long.toString(fileSize)) / 1024;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return Double.parseDouble(decimalFormat.format(size));
    }

    /**
     * 获取文件大小 (字节)
     *
     * @param file 文件
     * @return {@link Long}
     */
    public static Long getFileSize(MultipartFile file) {
        if (ObjectUtils.isNull(file)) {
            return 0L;
        }
        return file.getSize();
    }

    /**
     * 将字节数组转换为String类型哈希值
     *
     * @param fis 文件流
     * @return 哈希值
     */
    public static String getFileHashBySHA256(FileInputStream fis) {
        byte[] bytes = calculateSHA256(fis);
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 将字节数组转换为String类型哈希值
     *
     * @param fis 文件流
     * @return 哈希值
     */
    public static String getFileHashByMD5(FileInputStream fis) {
        byte[] bytes = calculateMd5(fis);
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 计算 SHA256
     *
     * @param fis 文件输入流
     * @return {@link byte[]}
     */
    public static byte[] calculateSHA256(FileInputStream fis) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算 MD5
     *
     * @param fis 文件输入流
     * @return {@link byte[]}
     */
    public static byte[] calculateMd5(FileInputStream fis) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取路径下的文件数量（只获取路径下第一级的文件数量）
     *
     * @param path 路径
     * @return {@link Map}<{@link String}, {@link Long}>
     */
    public static Map<String, Long> getFileCount(String path) {
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("fileCount", 0L);
        countMap.put("directoryCount", 0L);
        File file = new File(path);
        if (!file.exists()) {
            return countMap;
        }
        if (file.isFile()) {
            countMap.put("fileCount", 1L);
            return countMap;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return countMap;
        }
        for (File f : files) {
            if (f.isFile()) {
                countMap.put("fileCount", countMap.get("fileCount") + 1);
            } else {
                countMap.put("directoryCount", countMap.get("directoryCount") + 1);
            }
        }
        return countMap;
    }

    /**
     * 获取路径下的文件数量（递归获取路径下的文件数量）
     *
     * @param path 路径
     * @return {@link Map}<{@link String}, {@link Long}>
     */
    public static Map<String, Long> getFileCountDeep(String path) {
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("fileCount", 0L);
        countMap.put("directoryCount", 0L);

        Path startPath = Paths.get(path);
        if (!Files.exists(startPath)) {
            return countMap;
        }

        try (Stream<Path> stream = Files.walk(startPath)) {
            stream.forEach(filePath -> {
                if (Files.isDirectory(filePath)) {
                    countMap.put("directoryCount", countMap.get("directoryCount") + 1);
                } else if (Files.isRegularFile(filePath)) {
                    countMap.put("fileCount", countMap.get("fileCount") + 1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return countMap;
    }

    /**
     * 获取路径下的文件路径列表（只获取路径下第一级的文件路径，包含文件，不包括文件夹）
     *
     * @param path 文件路径
     * @return {@link List}<{@link String}>
     */
    public static List<String> getFilePathList(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        List<String> filePathList = new ArrayList<>();
        if (file.isFile()) {
            filePathList.add(file.getPath());
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                return filePathList;
            }
            for (File f : files) {
                if (f.isFile()) {
                    filePathList.add(f.getPath());
                }
            }
        }
        return filePathList;
    }

    /**
     * 获取路径下的文件路径列表（递归获取路径下的所有文件路径，只包含文件，不包含文件夹）
     *
     * @param path 文件路径
     * @return {@link List}<{@link String}>
     */
    public static List<String> getFilePathListDeep(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        List<String> filePathList = new ArrayList<>();
        if (file.isFile()) {
            filePathList.add(file.getPath());
        } else {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return filePathList;
            }
            for (File f : fileList) {
                filePathList.addAll(getFilePathListDeep(f.getPath()));
            }
        }
        return filePathList;
    }

    /**
     * 删除最后一个文件分隔符
     *
     * @param path 文件路径
     * @return {@link String}
     */
    public static String removeLastFileSeparator(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.emptyStr();
        }
        if (path.endsWith(FILE_SEPARATOR)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 追加最后一个文件分隔符
     *
     * @param path 文件路径
     * @return {@link String}
     */
    public static String appendLastFileSeparator(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.emptyStr();
        }
        if (!path.endsWith(FILE_SEPARATOR)) {
            path += FILE_SEPARATOR;
        }
        return path;
    }

    /**
     * 删除第一个文件分隔符
     *
     * @param path 文件路径
     * @return {@link String}
     */
    public static String removeFirstFileSeparator(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.emptyStr();
        }
        if (path.startsWith(FILE_SEPARATOR)) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 追加第一个文件分隔符
     *
     * @param path 文件路径
     * @return {@link String}
     */
    public static String appendFirstFileSeparator(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.emptyStr();
        }
        if (!path.startsWith(FILE_SEPARATOR)) {
            path = FILE_SEPARATOR + path;
        }
        return path;
    }
}
