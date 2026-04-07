package com.nexus.common.core.domain.ip;

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
public class IpHome implements Serializable {

    /**
     * ip地址
     */
    private String ip;

    /**
     * 国家
     */
    private String country;

    /**
     * 省
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * ip运营商
     */
    private String ipOperator;

    /**
     * 输出结果
     */
    private String result;
}
