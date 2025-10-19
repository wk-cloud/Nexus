package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysLoginLog;
import com.nexus.system.domain.vo.SysLoginLogVo;
import org.springframework.stereotype.Repository;

/**
 * 登录日志映射器
 *
 * @author wk
 * @date 2024/06/30
 */
@Repository
public interface SysLoginLogMapper extends BaseMapperPlus<SysLoginLog, SysLoginLogVo> {
}
