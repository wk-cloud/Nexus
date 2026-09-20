package com.nexus.test.excel.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PurchaseDataExportVO
 *
 * @author wk
 * @date 2026/6/10 12:45
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseDataExportVO {

    private Long id;

    @ExcelProperty(value = "采购单号", index = 0)
    private String purchaseNo;

    @ExcelProperty(value = "供应商名称", index = 1)
    private String supplierName;

    @ExcelProperty(value = "采购数量", index = 2)
    private Integer quantity;

    @ExcelProperty(value = "单价(元)", index = 3)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "总金额(元)", index = 4)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "采购日期", index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchaseTime;

    @ExcelProperty(value = "状态", index = 6)
    private String statusDesc;
}
