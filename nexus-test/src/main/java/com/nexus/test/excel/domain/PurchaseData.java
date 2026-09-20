package com.nexus.test.excel.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nexus.common.mybatisplus.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PurchaseDataExportVO
 *
 * @author wk
 * @date 2026/6/10 12:45
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("purchase_data")
public class PurchaseData extends BaseEntity {

    private String purchaseNo;

    private String supplierName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime purchaseTime;

    private String statusDesc;
}
