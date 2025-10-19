package com.nexus.common.utils;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 下载文件工具类
 *
 * @author wk
 * @date 2024/04/06
 */
@Slf4j
public class FileDownloadUtils {

    /**
     * 异步任务执行器
     */
    private static final Executor asyncTaskExecutor = SpringUtils.getBean("asyncTaskExecutor");

    /**
     * 下载 zip
     *
     * @param fileUrls 文件url列表
     * @param response 响应
     */
    public static void downloadZip(List<String> fileUrls, HttpServletResponse response) throws IOException {
        if (CollectionUtils.isEmpty(fileUrls)) {
            log.warn("====> 没有可下载的文件");
            return;
        }
        String fileName = String.format("%s.zip", DateUtils.formatDate(new Date()));
        // 设置响应头
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {

            // 并发下载所有文件并返回临时路径的 CompletableFuture 列表
            List<CompletableFuture<Path>> downloadFutures = fileUrls.stream()
                    .map(url -> CompletableFuture.supplyAsync(() -> downloadFileToTemp(url), asyncTaskExecutor))
                    .collect(Collectors.toList());

            // 合并所有 Future，并在完成后依次写入 zip 流
            CompletableFuture<Void> allDownloadsDone = CompletableFuture.allOf(
                    downloadFutures.toArray(new CompletableFuture[0])
            );

            // 所有下载完成后，将文件添加到 zip 中
            allDownloadsDone.thenRun(() -> {
                for (int i = 0; i < fileUrls.size(); i++) {
                    String targetFileName = FileUtils.extractFileNameFromUrl(fileUrls.get(i));
                    Path tempFile = downloadFutures.get(i).join(); // 获取下载结果

                    try {
                        addToZip(zos, tempFile, targetFileName);
                        Files.deleteIfExists(tempFile); // 删除临时文件
                    } catch (IOException e) {
                        throw new RuntimeException("无法将文件添加到 zip: " + targetFileName, e);
                    }
                }
            }).exceptionally(ex -> {
                log.error("将文件添加到 zip 时出错: {}", ex.getMessage());
                return null;
            }).get(); // 等待 zip 完成

        } catch (Exception e) {
            throw new IOException("下载或压缩文件时出错: ", e);
        }
    }

    /**
     * 将文件下载到临时目录
     *
     * @param fileUrl 文件 URL
     * @return {@link Path }
     */
    private static Path downloadFileToTemp(String fileUrl) {
        try {
            Path tempFile = Files.createTempFile("download-", ".tmp");

            try (InputStream in = new URL(fileUrl).openStream();
                 OutputStream out = Files.newOutputStream(tempFile)) {

                byte[] buffer = new byte[1024 * 8];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;

        } catch (IOException e) {
            throw new RuntimeException("无法从 URL 下载文件: " + fileUrl, e);
        }
    }

    /**
     * 添加到 zip
     *
     * @param zos       ZOS
     * @param filePath  文件路径
     * @param entryName 条目名称
     * @throws IOException io异常
     */
    private static void addToZip(ZipOutputStream zos, Path filePath, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);

        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = is.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }

        zos.closeEntry();
    }

    /**
     * 下载
     *
     * @param path     路径
     * @param response 响应
     */
    public static void download(String path, HttpServletResponse response) {
        if (StringUtils.isBlank(path)) {
            throw new RuntimeException("文件下载失败，失败原因：文件不存在");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("文件下载失败，失败原因：文件不存在");
        }
        download(file, response);
    }

    /**
     * 下载文件
     *
     * @param file     文件
     * @param response 响应
     */
    public static void download(File file, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            if (ObjectUtils.isNull(file)) {
                throw new RuntimeException("文件下载失败，文件资源不存在");
            }
            response = createResponse(response, file);
            byte[] bytes = FileUtils.readBytes(file);
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件
     *
     * @param bytes    字节
     * @param fileName 文件名
     * @param response 响应
     */
    public static void download(byte[] bytes, String fileName, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            if (ArrayUtils.isEmpty(bytes)) {
                throw new RuntimeException("文件下载失败，文件资源不存在");
            }
            response = createResponse(response, fileName);
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 创建响应
     *
     * @param response 响应
     * @param fileName 文件名称
     * @return {@link HttpServletResponse}
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    private static HttpServletResponse createResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("创建下载响应失败，文件名称不能为空");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return response;
    }

    /**
     * 创建响应
     *
     * @param response 响应
     * @param file     文件
     * @return {@link HttpServletResponse}
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    private static HttpServletResponse createResponse(HttpServletResponse response, File file) throws UnsupportedEncodingException {
        if (ObjectUtils.isNull(file)) {
            throw new RuntimeException("创建下载响应失败，文件资源不存在");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        response.setContentLength((int) file.length());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return response;
    }
}
