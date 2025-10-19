package com.nexus.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexus.common.core.page.PagingData;
import com.nexus.common.core.query.QueryParams;
import com.nexus.common.exception.ServiceException;
import com.nexus.common.utils.BeanUtils;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.system.domain.SysDictData;
import com.nexus.system.domain.dto.SysDictDataDto;
import com.nexus.system.domain.vo.SysDictDataVo;
import com.nexus.system.mapper.SysDictDataMapper;
import com.nexus.system.service.SysDictDataBackService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * 系统字典服务实现类
 *
 * @author wk
 * @date 2024/03/28
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class SysDictDataBackServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataBackService {
    @Resource
    private SysDictDataMapper baseMapper;

    /**
     * 通过系统字典类型删除
     *
     * @param dictTypes 系统字典类型
     * @return boolean
     */
    @Override
    public Boolean removeBySysDictType(Collection<String> dictTypes) {
        LambdaQueryWrapper<SysDictData> sysDictDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictDataLambdaQueryWrapper.in(SysDictData::getDictType,dictTypes);
        return this.remove(sysDictDataLambdaQueryWrapper);
    }

    /**
     * 更新系统字典数据
     *
     * @param sysDictDataDto 系统字典DTO
     * @return {@link Boolean}
     */
    @Override
    public Boolean updateSysDictData(SysDictDataDto sysDictDataDto) {
        if(!checkDictValueUnique(sysDictDataDto)){
            throw new ServiceException("修改失败，字典值 【" + sysDictDataDto.getDictValue() + "】 已存在");
        }
        SysDictData sysDictData = BeanUtils.toBean(sysDictDataDto, SysDictData.class);
        return this.updateById(sysDictData);
    }

    /**
     * 新增系统字典数据
     *
     * @param sysDictDataDto 系统字典DTO
     * @return {@link Boolean}
     */
    @Override
    public Boolean addSysDictData(SysDictDataDto sysDictDataDto) {
        if(!checkDictValueUnique(sysDictDataDto)){
            throw new ServiceException("新增失败，字典值 【" + sysDictDataDto.getDictValue() + "】 已存在");
        }
        SysDictData sysDictData = BeanUtils.toBean(sysDictDataDto, SysDictData.class);
        return this.save(sysDictData);
    }

    /**
     * 检查字典值是否唯一
     *
     * @param sysDictDataDto 字典数据
     * @return boolean
     */
    private boolean checkDictValueUnique(SysDictDataDto sysDictDataDto){
        LambdaQueryWrapper<SysDictData> sysDictDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictDataLambdaQueryWrapper
                .eq(SysDictData::getDictValue,sysDictDataDto.getDictValue())
                .eq(SysDictData::getDictType, sysDictDataDto.getDictType())
                .ne(ObjectUtils.isNotNull(sysDictDataDto.getId()),SysDictData::getId,sysDictDataDto.getId());
        return !baseMapper.exists(sysDictDataLambdaQueryWrapper);
    }

    /**
     * 查询系统字典数据列表
     *
     * @param sysDictDataDto 系统字典数据dto
     * @param queryParams 查询参数
     * @return {@link PagingData}<{@link SysDictDataVo}>
     */
    @Override
    public PagingData<SysDictDataVo> listSysDictData(SysDictDataDto sysDictDataDto, QueryParams queryParams) {
        LambdaQueryWrapper<SysDictData> sysDictDataLambdaQueryWrapper = this.buildLambdaQueryWrapper(sysDictDataDto);
        Page<SysDictDataVo> sysDictDataVoPage = baseMapper.queryVoPage(queryParams.build(), sysDictDataLambdaQueryWrapper, SysDictDataVo.class);
        return PagingData.build(sysDictDataVoPage);
    }

    /**
     * 构建 Lambda 查询包装器
     *
     * @param sysDictDataDto 系统字典数据
     * @return {@link LambdaQueryWrapper}<{@link SysDictData}>
     */
    private LambdaQueryWrapper<SysDictData> buildLambdaQueryWrapper(SysDictDataDto sysDictDataDto){
        LambdaQueryWrapper<SysDictData> sysDictDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysDictDataLambdaQueryWrapper.orderByAsc(SysDictData::getDictSort);
        if (ObjectUtils.isNotNull(sysDictDataDto)){
            sysDictDataLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysDictDataDto.getId()),SysDictData::getId,sysDictDataDto.getId());
            sysDictDataLambdaQueryWrapper.like(StringUtils.isNotBlank(sysDictDataDto.getDictLabel()),SysDictData::getDictLabel,sysDictDataDto.getDictLabel());
            sysDictDataLambdaQueryWrapper.like(StringUtils.isNotBlank(sysDictDataDto.getDictValue()),SysDictData::getDictValue,sysDictDataDto.getDictValue());
            sysDictDataLambdaQueryWrapper.eq(StringUtils.isNotBlank(sysDictDataDto.getDictType()),SysDictData::getDictType,sysDictDataDto.getDictType());
            sysDictDataLambdaQueryWrapper.eq(ObjectUtils.isNotNull(sysDictDataDto.getState()),SysDictData::getState,sysDictDataDto.getState());
        }
        return sysDictDataLambdaQueryWrapper;
    }

}
