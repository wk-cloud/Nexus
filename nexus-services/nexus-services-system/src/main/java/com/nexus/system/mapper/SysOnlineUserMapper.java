package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysOnlineUser;
import com.nexus.system.domain.vo.SysOnlineUserVo;
import org.springframework.stereotype.Repository;

/**
 * 在线用户映射器
 *
 * @author wk
 * @date 2024/07/06
 */
@Repository
public interface SysOnlineUserMapper extends BaseMapperPlus<SysOnlineUser, SysOnlineUserVo> {
}
