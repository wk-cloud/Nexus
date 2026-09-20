package com.nexus.test.excel.service;

import jakarta.servlet.http.HttpServletResponse;

public interface PurchaseDataService {

    void export(HttpServletResponse response);

    void exportByStream(HttpServletResponse response);

    void exportByMultiThread(HttpServletResponse response);
}
