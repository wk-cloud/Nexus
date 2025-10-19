package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysRole;
import com.nexus.system.domain.vo.SysRoleVo;
import org.springframework.stereotype.Repository;

/**
 * 角色映射器
 *
 * @author wk
 * @date 2023/12/16
 */
@Repository
public interface SysRoleMapper extends BaseMapperPlus<SysRole, SysRoleVo> {

}
