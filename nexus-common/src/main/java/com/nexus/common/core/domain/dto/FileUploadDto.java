package com.nexus.common.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件 DTO
 */
@Schema(title = "上传文件DTO", description = "上传文件DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileUploadDto {

    /**
     * 文件名
     */
    @Schema(name = "文件名称")
    private String fileName;

    /**
     * 文件后缀
     */
    @Schema(name = "文件后缀")
    private String fileSuffix;

    /**
     * 文件类型
     */
    @Schema(name = "文件类型")
    private String contentType;

    /**
     * 文件大小
     */
    @Schema(name = "文件大小")
    private Long size;

    /**
     * 文件哈希
     */
    @Schema(name = "文件hash值")
    private String fileHash;

    /**
     * 已上传大小
     */
    @Schema(name = "已上传大小")
    private Long uploadedSize;

    /**
     * 切片数量
     */
    @Schema(name = "切片数量")
    private Integer chunkCount;

    /**
     * 已上传成功的切片数量
     * */
    @Schema(name = "已上传成功的切片数量")
    private Integer uploadedChunkCount;

    /**
     * 切片尺寸
     */
    @Schema(name = "切片尺寸")
    private Long chunkSize;

    /**
     * 切片索引
     */
    @Schema(name = "切片索引")
    private Integer chunkIndex;

    /**
     * 文件
     */
    @Schema(name = "文件")
    private MultipartFile file;

}
