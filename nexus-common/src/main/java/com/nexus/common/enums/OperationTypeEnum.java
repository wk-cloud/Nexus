package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型 enum
 *
 * @author wk
 * @date 2024/12/07
 */
@Getter
@AllArgsConstructor
public enum OperationTypeEnum {
    ADD,
    UPDATE,
    DELETE,
    EXPORT,
    IMPORT,
    OFFLINE_USER,
    UPLOAD,
    DOWNLOAD,
    FILE_MERGE,
    OTHER;
}
