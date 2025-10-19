package com.nexus.system.mapper;

import com.nexus.common.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysUser;
import com.nexus.system.domain.vo.SysUserVo;
import org.springframework.stereotype.Repository;

/**
 * 用户映射器
 *
 * @author wk
 * @date 2023/12/31
 */
@Repository
public interface SysUserMapper extends BaseMapperPlus<SysUser, SysUserVo> {

}
