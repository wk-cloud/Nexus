package com.nexus.test.excel.controller;

import com.nexus.common.core.annotation.Pass;
import com.nexus.test.excel.service.PurchaseDataService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PurchaseDataController
 *
 * @author wk
 * @date 2026/6/10 12:55
 */
@RestController
@RequestMapping("/purchaseData")
public class PurchaseDataController {

    @Resource
    private PurchaseDataService purchaseDataService;

    @GetMapping("/export")
    @Pass
    public void export(HttpServletResponse response) {
        purchaseDataService.export(response);
    }

    @GetMapping("/exportByStream")
    @Pass
    public void exportByStream(HttpServletResponse response) {
        purchaseDataService.exportByStream(response);
    }

    @GetMapping("/exportByAsync")
    @Pass
    public void exportByAsync(HttpServletResponse response) {
        purchaseDataService.exportByMultiThread(response);
    }
}
