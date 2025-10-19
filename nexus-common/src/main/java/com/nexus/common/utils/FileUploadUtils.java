package com.nexus.common.utils;

import com.nexus.common.core.domain.dto.FileUploadDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
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
@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
@ConfigurationProperties(prefix = "upload")
public class FileUploadUtils {
    /**
     * 文件分隔符
     */
    private static final String FILE_SEPARATOR = "/";

    /**
     * 静态资源路径
     */
    private static final String RESOURCE_PATH = "/public";

    /**
     * 临时资源路径
     */
    private static final String TEMP_RESOURCE_PATH = "/temp";

    /**
     * 默认代理路径
     */
    private String defaultProxyPath = "/files";

    /**
     * 端口号
     */
    @Value("${server.port}")
    private Integer port;

    /**
     * 是否为生产环境
     */
    private boolean prod;

    /**
     * 服务器网址
     */
    private String baseUrl;

    /**
     * 文件保存的基目录
     */
    private String basePath;

    /**
     * 获取文件分隔符
     *
     * @return {@link String}
     */
    public String fileSeparator() {
        return FILE_SEPARATOR;
    }

    /**
     * 静态文件路径
     *
     * @return {@link String}
     */
    public String resourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * 临时文件路径
     *
     * @return {@link String}
     */
    public String tempResourcePath() {
        return TEMP_RESOURCE_PATH;
    }

    /**
     * 获取 baseUrl
     *
     * @return {@link String}
     */
    public String getBaseUrl() {
        return FileUtils.removeLastFileSeparator(baseUrl);
    }

    /**
     * 获取 basePath
     *
     * @return {@link String}
     */
    public String getBasePath() {
        if (isProd()) {
            return FileUtils.removeLastFileSeparator(basePath);
        }
        return FileUtils.getRootDirectoryPath(RESOURCE_PATH).concat(defaultProxyPath);
    }

    /**
     * 获取代理路径
     *
     * @return {@link String}
     */
    public String getProxyPath() {
        String proxyPath;
        if (isProd()) {
            proxyPath = defaultProxyPath;
        } else {
            proxyPath = RESOURCE_PATH + defaultProxyPath;
        }
        return FileUtils.appendFirstFileSeparator(proxyPath);
    }

    /**
     * 获取文件切片目录路径
     *
     * @param fileHash 文件哈希
     * @return {@link String}
     */
    public String getFileChunkDirectoryPath(String fileHash) {
        if(isProd()){
            return getBasePath()
                    .concat(defaultProxyPath)
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
    public String createFileChunkDirectory(String fileHash) {
        String path = this.getFileChunkDirectoryPath(fileHash);
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
    public boolean removeBatch(List<String> fileUrlList) {
        if (CollectionUtils.isEmpty(fileUrlList)) {
            return false;
        }
        fileUrlList.forEach(url -> {
            try {
                this.remove(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
    public boolean remove(String fileUrl) throws MalformedURLException {
        if (StringUtils.isBlank(fileUrl)) {
            return false;
        }

        URL url = new URL(fileUrl);

        String path = url.getPath();

        if (isProd()) {
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
    public String upload(MultipartFile multipartFile) throws IOException {
        // 代理路径
        String proxyPath = this.getProxyPath();
        // 生成文件名
        String fileName = FileUtils.randomFileName(multipartFile.getOriginalFilename());
        // 文件url
        String url;
        if (prod) {
            String filePath = FileUtils.createDateLevelDirectory(getBasePath() + proxyPath) + fileName;
            multipartFile.transferTo(new File(filePath));
            url = getBaseUrl() + filePath.substring(getBasePath().length());
        } else {
            String directoryPath = FileUtils.getDateLevelDirectoryPath(proxyPath);
            String filePath = FileUtils.createRootDirectory(directoryPath) + fileName;
            multipartFile.transferTo(new File(filePath));
            url = getBaseUrl() + ":" + port
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
    public String upload(File file) throws IOException {
        // 代理路径
        String proxyPath = this.getProxyPath();
        // 获取文件名
        String fileName = FileUtils.randomFileName(file.getName());
        // 文件url
        String url;
        if (prod) {
            String filePath = FileUtils.createDateLevelDirectory(getBasePath() + proxyPath) + fileName;
            FileUtils.touch(new File(filePath));
            url = getBaseUrl() + filePath.substring(getBasePath().length());
        } else {
            String directoryPath = FileUtils.getDateLevelDirectoryPath(proxyPath);
            String filePath = FileUtils.createRootDirectory(directoryPath) + fileName;
            FileUtils.touch(new File(filePath));
            url = getBaseUrl() + ":" + port
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
    public void uploadChunk(FileUploadDto fileUploadDto) throws IOException {
        // 生成文件名
        String fileName = fileUploadDto.getChunkIndex() + FileUtils.getCompleteSuffix(fileUploadDto.getFileName());
        // 生成文件路径
        String path = this.createFileChunkDirectory(fileUploadDto.getFileHash()).concat(fileName);
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
    public String mergeChunk(String fileHash, String fileName) {
        // 获取文件切片
        String fileChunkDirectoryPath = this.getFileChunkDirectoryPath(fileHash);
        List<File> chunkList = FileUtils
                .loopFiles(fileChunkDirectoryPath).stream()
                .sorted(Comparator.comparingInt(a -> Integer.valueOf(FileUtils.getPrefix(a.getName()))))
                .collect(Collectors.toList());
        // 代理路径
        String proxyPath = this.getProxyPath();
        // 文件url
        String url;
        if (prod) {
            String filePath = this.getBasePath() + proxyPath;
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
            url = getBaseUrl() + ":" + port
                    + directoryPath.replaceAll("\\\\", "/").replaceFirst(RESOURCE_PATH, "")
                    + fileName;
        }
        // 删除分片文件
        FileUtils.del(fileChunkDirectoryPath);
        // 返回url
        return url;
    }
}
