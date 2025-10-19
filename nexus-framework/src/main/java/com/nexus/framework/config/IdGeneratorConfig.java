package com.nexus.framework.config;


import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.nexus.common.utils.IdUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ID 生成器配置
 *
 * @author wk
 * @date 2024/01/13
 */
@Slf4j
@Configuration
public class IdGeneratorConfig {

    /**
     * 初始化 ID 生成器
     */
    //@Bean
    @PostConstruct
    public void initIdGenerator(){
        IdGeneratorOptions idGeneratorOptions = new IdGeneratorOptions(IdUtils.workerId);
        idGeneratorOptions.WorkerIdBitLength = IdUtils.workerIdBitLength;
        idGeneratorOptions.SeqBitLength = IdUtils.seqBitLength;
        idGeneratorOptions.BaseTime = IdUtils.BASE_TIME;
        YitIdHelper.setIdGenerator(idGeneratorOptions);
        log.info("====> id生成器初始化完成");
    }

}
