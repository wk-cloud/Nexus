package com.nexus.common.utils;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 工具类
 *
 * @author wk
 * @date 2024/04/18
 */
public class ExcelUtils extends ExcelUtil {

    /**
     * 导出 Excel
     *
     * @param response  HttpServletResponse
     * @param dataList  数据列表
     * @param fileName  导出文件名
     * @param sheetName 工作表名称
     * @param clazz     数据对应实体类
     * @throws IOException io异常
     */
    public static <T> void export(HttpServletResponse response, List<T> dataList, Class<T> clazz, String fileName, String sheetName) {
        ServletOutputStream outputStream = null;
        try {
            response = createResponse(response, fileName);
            outputStream = response.getOutputStream();
            EasyExcel.write(outputStream, clazz)
                    .autoCloseStream(Boolean.FALSE)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(sheetName).doWrite(dataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 创建响应
     *
     * @param response 响应
     * @param fileName 文件名
     * @return {@link HttpServletResponse}
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    private static HttpServletResponse createResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(fileName)) {
            fileName = FileUtils.randomFileName("*.xlsx");
        }
        if (!hasExcelFileSuffix(fileName)) {
            throw new RuntimeException("创建下载响应失败，文件名必须以 .xlsx 或 .xls 结尾");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return response;
    }

    /**
     * 具有 Excel 文件后缀
     *
     * @param fileName 文件名
     * @return boolean
     */
    private static boolean hasExcelFileSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        return fileName.matches(".*\\.(xls|xlsx)$");
    }

}
