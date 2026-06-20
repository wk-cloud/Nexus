package com.nexus.common.oss.utils;

import com.nexus.common.core.utils.CollectionUtils;
import com.nexus.common.core.utils.FileUtils;
import com.nexus.common.core.utils.SpringUtils;
import com.nexus.common.core.utils.StringUtils;
import com.nexus.common.oss.config.properties.FileUploadProperties;
import com.nexus.common.oss.domain.FileUploadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 上传文件工具类
 *
 * @author wk
 * @date 2022/7/24
 */
@Slf4j
public class FileUploadUtils {

    private static final FileUploadProperties fileUploadProperties = SpringUtils.getBean(FileUploadProperties.class);

    /**
     * 文件分隔符
     */
    private static final String FILE_SEPARATOR = "/";

    /**
     * 静态资源路径
     */
    private static final String RESOURCE_PATH;

    /**
     * 临时资源路径
     */
    private static final String TEMP_RESOURCE_PATH;

    /**
     * 默认代理路径
     */
    private static final String DEFAULT_PROXY_PATH;

    /**
     * 端口号
     */
    private static final Integer PORT;

    /**
     * 是否为生产环境
     */
    private static final Boolean IS_PROD;

    /**
     * 服务器网址
     */
    private static final String BASE_URL;

    /**
     * 文件保存的基目录
     */
    private static final String BASE_PATH;

    static {
        RESOURCE_PATH = fileUploadProperties.getResourcePath();
        TEMP_RESOURCE_PATH = fileUploadProperties.getTempResourcePath();
        DEFAULT_PROXY_PATH = fileUploadProperties.getDefaultProxyPath();
        PORT = fileUploadProperties.getPort();
        IS_PROD = fileUploadProperties.isProd();
        BASE_URL = fileUploadProperties.getBaseUrl();
        BASE_PATH = fileUploadProperties.getBasePath();
    }

    /**
     * 是否是生产环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isProd() {
        return IS_PROD;
    }

    /**
     * 获取文件分隔符
     *
     * @return {@link String}
     */
    public static String fileSeparator() {
        return FILE_SEPARATOR;
    }

    /**
     * 静态文件路径
     *
     * @return {@link String}
     */
    public static String resourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * 临时文件路径
     *
     * @return {@link String}
     */
    public static String tempResourcePath() {
        return TEMP_RESOURCE_PATH;
    }

    /**
     * 获取 baseUrl
     *
     * @return {@link String}
     */
    public static String getBaseUrl() {
        return FileUtils.removeLastFileSeparator(BASE_URL);
    }

    /**
     * 获取 basePath
     *
     * @return {@link String}
     */
    public static String getBasePath() {
        if (IS_PROD) {
            return FileUtils.removeLastFileSeparator(BASE_PATH);
        }
        return FileUtils.getRootDirectoryPath(RESOURCE_PATH).concat(DEFAULT_PROXY_PATH);
    }

    /**
     * 获取代理路径
     *
     * @return {@link String}
     */
    public static String getProxyPath() {
        String proxyPath;
        if (IS_PROD) {
            proxyPath = DEFAULT_PROXY_PATH;
        } else {
            proxyPath = RESOURCE_PATH + DEFAULT_PROXY_PATH;
        }
        return FileUtils.appendFirstFileSeparator(proxyPath);
    }

    /**
     * 获取文件切片目录路径
     *
     * @param fileHash 文件哈希
     * @return {@link String}
     */
    public static String getFileChunkDirectoryPath(String fileHash) {
        if (IS_PROD) {
            return getBasePath()
                    .concat(DEFAULT_PROXY_PATH)
                    .concat(TEMP_RESOURCE_PATH)
                    .concat(FILE_SEPARATOR)
                    .concat(fileHash)
                    .concat(FILE_SEPARATOR);
        }
        return getBasePath()
                .concat(TEMP_RESOURCE_PATH)
                .concat(FILE_SEPARATOR)
                .concat(fileHash)
                .concat(FILE_SEPARATOR);
    }

    /**
     * 创建文件切片目录
     *
     * @param fileHash 文件哈希
     * @return {@link String}
     */
    public static String createFileChunkDirectory(String fileHash) {
        String path = getFileChunkDirectoryPath(fileHash);
        if (!FileUtils.exist(path)) {
            FileUtils.mkdir(path);
        }
        return path;
    }

    /**
     * 批量删除文件
     *
     * @param fileUrlList 文件 URL 列表
     * @return boolean
     */
    public static boolean removeBatch(List<String> fileUrlList) {
        if (CollectionUtils.isEmpty(fileUrlList)) {
            return false;
        }
        fileUrlList.forEach(url -> {
            try {
                remove(url);
            } catch (MalformedURLException e) {
                log.error("文件删除失败", e);
            }
        });
        return true;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件url地址
     * @return boolean
     */
    public static boolean remove(String fileUrl) throws MalformedURLException {
        if (StringUtils.isBlank(fileUrl)) {
            return false;
        }

        URL url = new URL(fileUrl);

        String path = url.getPath();

        if (IS_PROD) {
            path = getBasePath() + path;
        } else {
            path = FileUtils.getRootDirectoryPath(null) + FileUtils.toWindowsFileSeparator(RESOURCE_PATH + path);
        }

        File file = new File(path);

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 上传文件 (上传到本地)
     *
     * @param multipartFile 文件
     * @return {@link String}
     * @throws IOException io异常
     */
    public static String upload(MultipartFile multipartFile) throws IOException {
        // 代理路径
        String proxyPath = getProxyPath();
        // 生成文件名
        String fileName = FileUtils.randomFileName(multipartFile.getOriginalFilename());
        // 文件url
        String url;
        if (IS_PROD) {
            String filePath = FileUtils.createDateLevelDirectory(getBasePath() + proxyPath) + fileName;
            multipartFile.transferTo(new File(filePath));
            url = getBaseUrl() + filePath.substring(getBasePath().length());
        } else {
            String directoryPath = FileUtils.getDateLevelDirectoryPath(proxyPath);
            String filePath = FileUtils.createRootDirectory(directoryPath) + fileName;
            multipartFile.transferTo(new File(filePath));
            url = getBaseUrl() + ":" + PORT
                    + directoryPath.replaceAll("\\\\", "/").replaceFirst(RESOURCE_PATH, "")
                    + fileName;
        }
        return url;
    }

    /**
     * 上传文件 (上传到本地)
     *
     * @param file 文件
     * @return {@link String}
     * @throws IOException io异常
     */
    public static String upload(File file) throws IOException {
        // 代理路径
        String proxyPath = getProxyPath();
        // 获取文件名
        String fileName = FileUtils.randomFileName(file.getName());
        // 文件url
        String url;
        if (IS_PROD) {
            String filePath = FileUtils.createDateLevelDirectory(getBasePath() + proxyPath) + fileName;
            FileUtils.touch(new File(filePath));
            url = getBaseUrl() + filePath.substring(getBasePath().length());
        } else {
            String directoryPath = FileUtils.getDateLevelDirectoryPath(proxyPath);
            String filePath = FileUtils.createRootDirectory(directoryPath) + fileName;
            FileUtils.touch(new File(filePath));
            url = getBaseUrl() + ":" + PORT
                    + directoryPath.replaceAll("\\\\", "/").replaceFirst(RESOURCE_PATH, "")
                    + fileName;
        }
        return url;
    }

    /**
     * 上传文件切片
     *
     * @param fileUploadDto 上传文件 DTO
     * @throws IOException io异常
     */
    public static void uploadChunk(FileUploadDto fileUploadDto) throws IOException {
        // 生成文件名
        String fileName = fileUploadDto.getChunkIndex() + FileUtils.getCompleteSuffix(fileUploadDto.getFileName());
        // 生成文件路径
        String path = createFileChunkDirectory(fileUploadDto.getFileHash()).concat(fileName);
        // 保存文件
        fileUploadDto.getFile().transferTo(new File(path));
    }

    /**
     * 合并文件切片
     * todo 文件写入优化
     *
     * @param fileHash 文件哈希
     * @param fileName 文件名
     */
    public static String mergeChunk(String fileHash, String fileName) {
        // 获取文件切片
        String fileChunkDirectoryPath = getFileChunkDirectoryPath(fileHash);
        List<File> chunkList = FileUtils
                .loopFiles(fileChunkDirectoryPath).stream()
                .sorted(Comparator.comparingInt(a -> Integer.parseInt(FileUtils.getPrefix(a.getName()))))
                .collect(Collectors.toList());
        // 代理路径
        String proxyPath = getProxyPath();
        // 文件url
        String url;
        if (IS_PROD) {
            String filePath = getBasePath() + proxyPath;
            filePath = FileUtils.createDateLevelDirectory(filePath) + fileName;
            File distFile = new File(filePath);
            for (File chunkFile : chunkList) {
                FileUtils.writeBytes(FileUtils.readBytes(chunkFile), distFile, 0, (int) chunkFile.length(), true);
            }
            url = getBaseUrl() + filePath.substring(getBasePath().length());
        } else {
            String directoryPath = FileUtils.getDateLevelDirectoryPath(proxyPath);
            String filePath = FileUtils.createRootDirectory(directoryPath) + fileName;
            File distFile = new File(filePath);
            for (File chunkFile : chunkList) {
                FileUtils.writeBytes(FileUtils.readBytes(chunkFile), distFile, 0, (int) chunkFile.length(), true);
            }
            url = getBaseUrl() + ":" + PORT
                    + directoryPath.replaceAll("\\\\", "/").replaceFirst(RESOURCE_PATH, "")
                    + fileName;
        }
        // 删除分片文件
        FileUtils.del(fileChunkDirectoryPath);
        // 返回url
        return url;
    }
}
