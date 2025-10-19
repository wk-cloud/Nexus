package com.nexus.system.service;


import com.nexus.common.core.mapper.IServicePlus;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.system.domain.SysDictType;
import com.nexus.system.domain.dto.SysDictTypeDto;
import com.nexus.system.domain.vo.SysDictTypeVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统字典类型服务
 *
 * @author wk
 * @date 2024/03/28
 */
public interface SysDictTypeBackService extends IServicePlus<SysDictType, SysDictTypeVo> {

    /**
     * 查询字典类型
     *
     * @param dictType 字典类型
     * @return {@link SysDictTypeVo}
     */
    SysDictTypeVo queryDictType(String dictType);

    /**
     * 查询字典类型
     *
     * @param id 主键id
     * @return {@link SysDictTypeVo}
     */
    SysDictTypeVo queryDictType(Long id);

    /**
     * 按 ID 删除 dict 类型
     *
     * @param dictTypeIds 字典 ID
     * @return boolean
     */
    Boolean deleteDictTypeByIds(Collection<Long> dictTypeIds);

    /**
     * 更新系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型后台 DTO
     * @return {@link Boolean}
     */
    Boolean updateDictType(SysDictTypeDto sysDictTypeDto);

    /**
     * 新增系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型后台 DTO
     * @return {@link Boolean}
     */
    Boolean addDictType(SysDictTypeDto sysDictTypeDto);

    /**
     * 判断字典类型是否唯一
     *
     * @param sysDictTypeDto 系统字典类型dto
     * @return {@link Boolean}
     */
    Boolean checkDictTypeUnique(SysDictTypeDto sysDictTypeDto);

    /**
     * 查询系统字典类型列表
     *
     * @param sysDictTypeDto  系统字典类型dto
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysDictTypeVo}>
     */
    PagingData<SysDictTypeVo> querySysDictTypeList(SysDictTypeDto sysDictTypeDto, QueryParams queryParams);

    /**
     * 获取系统字典类型下拉选项数据
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link String }>>
     */
    List<Map<String, String>> getSelectOptions();
}
