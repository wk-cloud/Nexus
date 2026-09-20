package com.nexus.test.excel.mapper;

import com.nexus.common.mybatisplus.core.mapper.BaseMapperPlus;
import com.nexus.test.excel.domain.PurchaseData;
import com.nexus.test.excel.domain.PurchaseDataExportVO;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDataMapper extends BaseMapperPlus<PurchaseData, PurchaseDataExportVO> {

    @Select("SELECT * FROM purchase_data ORDER BY id")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = 1000)
    @ResultType(PurchaseDataExportVO.class)
    void streamQueryAll(ResultHandler<PurchaseDataExportVO> handler);

    @Select("SELECT * FROM purchase_data ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    @ResultType(PurchaseDataExportVO.class)
    List<PurchaseDataExportVO> queryByPage(@Param("offset") int offset, @Param("limit") int limit);
}
