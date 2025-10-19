package com.nexus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.CollectionUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.system.domain.SysDictData;
import com.nexus.system.domain.SysDictType;
import com.nexus.system.domain.dto.SysDictTypeDto;
import com.nexus.system.domain.vo.SysDictDataVo;
import com.nexus.system.domain.vo.SysDictTypeVo;
import com.nexus.system.mapper.SysDictTypeMapper;
import com.nexus.system.service.SysDictDataBackService;
import com.nexus.system.service.SysDictTypeBackService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 系统字典类型服务实现
 *
 * @author wk
 * @date 2024/03/28
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class SysDictTypeBackServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeBackService {

    @Resource
    private SysDictTypeMapper baseMapper;
    @Resource
    private SysDictDataBackService sysDictDataBackService;

    /**
     * 查询字典类型
     *
     * @param dictType 字典类型
     * @return {@link SysDictTypeVo}
     */
    @Override
    public SysDictTypeVo queryDictType(String dictType) {
        LambdaQueryWrapper<SysDictType> sysDictTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictTypeLambdaQueryWrapper.eq(SysDictType::getDictType,dictType);
        SysDictTypeVo sysDictTypeVo = baseMapper.queryVoOne(sysDictTypeLambdaQueryWrapper, SysDictTypeVo.class);
        if(ObjectUtils.isNull(sysDictTypeVo)){
            return null;
        }
        LambdaQueryWrapper<SysDictData> sysDictDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictDataLambdaQueryWrapper.eq(SysDictData::getDictType,sysDictTypeVo.getDictType());
        List<SysDictDataVo> sysDictDataList = sysDictDataBackService.queryVoList(sysDictDataLambdaQueryWrapper, SysDictDataVo.class);
        sysDictTypeVo.setDictDataList(sysDictDataList);
        return sysDictTypeVo;
    }

    /**
     * 查询字典类型
     *
     * @param id 主键id
     * @return {@link SysDictTypeVo}
     */
    @Override
    public SysDictTypeVo queryDictType(Long id) {
        return baseMapper.queryVoById(id, SysDictTypeVo.class);
    }

    /**
     * 按 ID 删除 dict 类型
     *
     * @param ids 字典 ID
     * @return boolean
     */
    @Override
    public Boolean deleteDictTypeByIds(Collection<Long> ids) {
        List<SysDictType> dictList = baseMapper.selectBatchIds(ids);
        if(!CollectionUtils.isEmpty(dictList)){
            List<String> dictTypeList = dictList.stream().map(SysDictType::getDictType).collect(Collectors.toList());
            // 删除字典类型下的所有字典数据
            sysDictDataBackService.removeBySysDictType(dictTypeList);
        }
        return this.removeBatchByIds(ids);
    }

    /**
     * 更新系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型 DTO
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateDictType(SysDictTypeDto sysDictTypeDto) {
        if(!this.checkDictTypeUnique(sysDictTypeDto)){
            throw new ServiceException("修改字典【 " + sysDictTypeDto.getDictName() + "】失败，字典类型已存在");
        }
        SysDictType sysDictType = BeanUtils.toBean(sysDictTypeDto, SysDictType.class);
        return this.updateById(sysDictType);
    }

    /**
     * 新增系统字典类型
     *
     * @param sysDictTypeDto 系统字典类型 DTO
     * @return {@link Boolean}
     */
    @Override
    public Boolean addDictType(SysDictTypeDto sysDictTypeDto) {
        if(!this.checkDictTypeUnique(sysDictTypeDto)){
            throw new ServiceException("新增字典【 " + sysDictTypeDto.getDictName() + "】失败，字典类型已存在");
        }
        SysDictType sysDictType = BeanUtils.toBean(sysDictTypeDto, SysDictType.class);
        return this.save(sysDictType);
    }

    /**
     * 判断字典类型是否唯一
     *
     * @param sysDictTypeDto 系统字典类型
     * @return {@link Boolean}
     */
    @Override
    public Boolean checkDictTypeUnique(SysDictTypeDto sysDictTypeDto) {
        SysDictType sysDictType = BeanUtils.toBean(sysDictTypeDto, SysDictType.class);
        LambdaQueryWrapper<SysDictType> sysDictTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictTypeLambdaQueryWrapper
                .eq(SysDictType::getDictType, sysDictType.getDictType())
                .ne(ObjectUtils.isNotNull(sysDictType.getId()), SysDictType::getId, sysDictType.getId());
        return !baseMapper.exists(sysDictTypeLambdaQueryWrapper);
    }

    /**
     * 查询系统字典类型列表
     *
     * @param sysDictTypeDto 系统字典类型dto
     * @param queryParams        查询参数
     * @return {@link PagingData}<{@link SysDictTypeVo}>
     */
    @Override
    public PagingData<SysDictTypeVo> querySysDictTypeList(SysDictTypeDto sysDictTypeDto, QueryParams queryParams) {
        LambdaQueryWrapper<SysDictType> sysDictTypeLambdaQueryWrapper = this.buildLambdaQueryWrapper(sysDictTypeDto);
        Page<SysDictTypeVo> sysDictTypeBackVoPage = baseMapper.queryVoPage(queryParams.build(), sysDictTypeLambdaQueryWrapper, SysDictTypeVo.class);
        return PagingData.build(sysDictTypeBackVoPage);
    }

    /**
     * 获取系统字典类型下拉选项数据
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link String }>>
     */
    @Override
    public List<Map<String, String>> getSelectOptions() {
        return baseMapper.selectList(null).stream().map(item -> {
            Map<String, String> map = new HashMap<>(2);
            map.put("label", item.getDictName());
            map.put("value", item.getDictType());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysDictTypeDto 系统字典类型dto
     * @return {@link LambdaQueryWrapper}<{@link SysDictType}>
     */
    private LambdaQueryWrapper<SysDictType> buildLambdaQueryWrapper(SysDictTypeDto sysDictTypeDto) {
        LambdaQueryWrapper<SysDictType> sysDictTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotNull(sysDictTypeDto)) {
            sysDictTypeLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysDictTypeDto.getId()), SysDictType::getId, sysDictTypeDto.getId());
            sysDictTypeLambdaQueryWrapper.like(StringUtils.isNotBlank(sysDictTypeDto.getDictName()), SysDictType::getDictName, sysDictTypeDto.getDictName());
            sysDictTypeLambdaQueryWrapper.like(StringUtils.isNotBlank(sysDictTypeDto.getDictType()), SysDictType::getDictType, sysDictTypeDto.getDictType());
            sysDictTypeLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysDictTypeDto.getState()), SysDictType::getState, sysDictTypeDto.getState());
        }
        return sysDictTypeLambdaQueryWrapper;
    }

}
