package com.nexus.test.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.nexus.common.core.utils.FileUtils;
import com.nexus.common.excel.utils.ExcelUtils;
import com.nexus.test.excel.domain.PurchaseDataExportVO;
import com.nexus.test.excel.mapper.PurchaseDataMapper;
import com.nexus.test.excel.service.PurchaseDataService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * PurchaseDataServiceImpl
 *
 * @author wk
 * @date 2026/6/10 12:51
 */
@Slf4j
@Service
public class PurchaseDataServiceImpl implements PurchaseDataService {

    @Resource
    private PurchaseDataMapper purchaseDataMapper;
    @Resource
    private Executor asyncTaskExecutor;

    private final int STREAM_BATCH_SIZE = 10000;

    private final int EXPORT_THREAD_BATCH_SIZE = 10000;

    @Override
    public void export(HttpServletResponse response) {
        long start = System.currentTimeMillis();
        ExcelUtils.export(response, purchaseDataMapper.queryVoList(), PurchaseDataExportVO.class, "采购数据.xlsx", "采购数据");
        long end = System.currentTimeMillis();
        log.info("导出耗时：{} s", (end - start)/1000);
    }

    @Override
    public void exportByStream(HttpServletResponse response) {
        long start = System.currentTimeMillis();
        try {
            createResponse(response, "采购数据.xlsx");

            ServletOutputStream outputStream = response.getOutputStream();

            ExcelWriter excelWriter = EasyExcel.write(outputStream, PurchaseDataExportVO.class)
                    .autoCloseStream(Boolean.FALSE)
                    .excelType(ExcelTypeEnum.XLSX)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet("采购数据").build();

            List<PurchaseDataExportVO> batchList = new ArrayList<>(STREAM_BATCH_SIZE);

            purchaseDataMapper.streamQueryAll(resultContext -> {
                PurchaseDataExportVO vo = resultContext.getResultObject();
                batchList.add(vo);

                if (batchList.size() >= STREAM_BATCH_SIZE) {
                    excelWriter.write(batchList, writeSheet);
                    batchList.clear();
                }
            });

            if (!batchList.isEmpty()) {
                excelWriter.write(batchList, writeSheet);
                batchList.clear();
            }

            excelWriter.finish();
            outputStream.flush();

        } catch (IOException e) {
            log.error("导出采购数据失败", e);
            throw new RuntimeException("导出失败", e);
        }
        long end = System.currentTimeMillis();
        log.info("导出耗时：{} s", (end - start)/1000);
    }

    @Override
    public void exportByMultiThread(HttpServletResponse response) {
        long start = System.currentTimeMillis();
        try {
            createResponse(response, "采购数据.xlsx");

            ServletOutputStream outputStream = response.getOutputStream();

            ExcelWriter excelWriter = EasyExcel.write(outputStream, PurchaseDataExportVO.class)
                    .autoCloseStream(Boolean.FALSE)
                    .excelType(ExcelTypeEnum.XLSX)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet("采购数据").build();

            long totalCount = purchaseDataMapper.selectCount(null);
            log.info("总记录数: {}", totalCount);

            if (totalCount == 0) {
                excelWriter.finish();
                outputStream.flush();
                return;
            }

            int totalPages = (int) Math.ceil((double) totalCount / EXPORT_THREAD_BATCH_SIZE);

            AtomicInteger processedCount = new AtomicInteger(0);

            List<CompletableFuture<List<PurchaseDataExportVO>>> futures = new ArrayList<>();

            for (int page = 0; page < totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<List<PurchaseDataExportVO>> future = CompletableFuture.supplyAsync(() -> {
                    int offset = currentPage * EXPORT_THREAD_BATCH_SIZE;
                    List<PurchaseDataExportVO> dataList = purchaseDataMapper.queryByPage(offset, EXPORT_THREAD_BATCH_SIZE);
                    log.info("批次 {} 查询完成，数量: {}, 偏移量: {}", currentPage + 1, dataList.size(), offset);
                    return dataList;
                }, asyncTaskExecutor);

                futures.add(future);
            }

            List<List<PurchaseDataExportVO>> allBatches = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            for (List<PurchaseDataExportVO> batch : allBatches) {
                if (!batch.isEmpty()) {
                    excelWriter.write(batch, writeSheet);
                    processedCount.addAndGet(batch.size());
                    log.info("已写入 {} 条记录", processedCount.get());
                }
            }

            excelWriter.finish();
            outputStream.flush();

        } catch (IOException e) {
            log.error("导出采购数据失败", e);
            throw new RuntimeException("导出失败", e);
        }
        long end = System.currentTimeMillis();
        log.info("多线程导出耗时：{} s", (end - start)/1000);
    }

    private void createResponse(HttpServletResponse response, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = FileUtils.randomFileName("*.xlsx");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
