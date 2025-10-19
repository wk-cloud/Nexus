package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysPermission;
import com.nexus.system.domain.vo.SysPermissionVo;
import org.springframework.stereotype.Repository;

/**
 * 权限映射器
 *
 * @author wk
 * @date 2023/12/31
 */
@Repository
public interface SysPermissionMapper extends BaseMapperPlus<SysPermission, SysPermissionVo> {


}
