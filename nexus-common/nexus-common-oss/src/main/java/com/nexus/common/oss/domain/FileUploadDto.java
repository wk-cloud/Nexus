package com.nexus.common.oss.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileUploadDto {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件哈希
     */
    private String fileHash;

    /**
     * 已上传大小
     */
    private Long uploadedSize;

    /**
     * 切片数量
     */
    private Integer chunkCount;

    /**
     * 已上传成功的切片数量
     * */
    private Integer uploadedChunkCount;

    /**
     * 切片尺寸
     */
    private Long chunkSize;

    /**
     * 切片索引
     */
    private Integer chunkIndex;

    /**
     * 文件
     */
    private MultipartFile file;

}
