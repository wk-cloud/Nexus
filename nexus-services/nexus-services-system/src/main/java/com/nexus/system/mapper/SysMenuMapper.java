package com.nexus.system.mapper;

import com.nexus.common.mybatisplus.core.mapper.BaseMapperPlus;
import com.nexus.system.domain.SysMenu;
import com.nexus.system.domain.vo.SysMenuVo;
import org.springframework.stereotype.Repository;

/**
 * 权限映射器
 *
 * @author wk
 * @date 2023/12/31
 */
@Repository
public interface SysMenuMapper extends BaseMapperPlus<SysMenu, SysMenuVo> {


}
