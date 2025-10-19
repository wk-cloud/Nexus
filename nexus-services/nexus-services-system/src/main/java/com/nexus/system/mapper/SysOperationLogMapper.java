package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysOperationLog;
import com.nexus.system.domain.vo.SysOperationLogVo;
import org.springframework.stereotype.Repository;

/**
 * 博客日志映射器
 *
 * @author wk
 * @date 2023/12/31
 */
@Repository
public interface SysOperationLogMapper extends BaseMapperPlus<SysOperationLog, SysOperationLogVo> {
}
