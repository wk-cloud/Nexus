package com.nexus.common.core.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * IP 归属
 *
 * @author wk
 * @date 2022/10/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(title = "ip归属实体类",description = "ip归属实体类")
public class IpHome implements Serializable {

    @Schema(name = "ip地址")
    private String ip;

    @Schema(name = "国家")
    private String country;

    @Schema(name = "省份")
    private String province;

    @Schema(name = "城市")
    private String city;

    @Schema(name = "运营商")
    private String ipOperator;

    @Schema(name = "输出结果")
    private String result;
}
