package com.nexus.framework.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.nexus.common.utils.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义主键id生成策略
 *
 * @author wk
 * @date 2023/12/30
 */
@Slf4j
@AllArgsConstructor
@Data
public class CustomerIdGenerator implements IdentifierGenerator {

    private final int ID_LENGTH = 16;

    @Override
    public Number nextId(Object entity) {
        Long snowflakeNextId = IdUtils.snowflakeId();
        log.info("====> 自定义主键策略： {}", snowflakeNextId);
        return snowflakeNextId;
    }
}
