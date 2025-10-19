package com.nexus.system.service;

import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysDictData;
import com.nexus.system.domain.dto.SysDictDataDto;
import com.nexus.system.domain.vo.SysDictDataVo;

import java.util.Collection;

/**
 * 系统字典服务
 *
 * @author wk
 * @date 2024/03/28
 */
public interface SysDictDataBackService extends IServicePlus<SysDictData, SysDictDataVo> {

    /**
     * 通过系统字典类型删除
     *
     * @param dictTypes 系统字典类型
     * @return boolean
     */
    Boolean removeBySysDictType(Collection<String> dictTypes);

    /**
     * 查询系统字典数据列表
     *
     * @param sysDictDataDto 系统字典数据
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysDictDataVo}>
     */
    PagingData<SysDictDataVo> listSysDictData(SysDictDataDto sysDictDataDto, QueryParams queryParams);

    /**
     * 更新系统字典数据
     *
     * @param sysDictDataDto 系统字典DTO
     * @return {@link Boolean}
     */
    Boolean updateSysDictData(SysDictDataDto sysDictDataDto);

    /**
     * 新增系统字典数据
     *
     * @param sysDictDataDto 系统字典DTO
     * @return {@link Boolean}
     */
    Boolean addSysDictData(SysDictDataDto sysDictDataDto);
}
