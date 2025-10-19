package com.nexus.auth.stragegy;

import com.nexus.common.core.domain.dto.LoginDto;
import com.nexus.common.core.domain.vo.LoginVo;

/**
 * 登录策略
 * @author wk
 * @date 2025/04/05
 */
public interface  LoginStrategy {

   /**
    * 登录通用方法
    * @param loginDto 登录信息
    * @return {@link LoginVo}
    */
   LoginVo login(LoginDto loginDto);
}
