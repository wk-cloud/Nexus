package com.nexus.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略枚举
 *
 * @author wk
 * @date 2026/10/06
 */
@Getter
@AllArgsConstructor
public enum ThreadPoolPolicyEnum {

    ABORT("Abort", new ThreadPoolExecutor.AbortPolicy()),
    CALLER_RUNS("CallerRuns", new ThreadPoolExecutor.CallerRunsPolicy()),
    DISCARD("Discard", new ThreadPoolExecutor.DiscardPolicy()),
    DISCARD_OLD("DiscardOld", new ThreadPoolExecutor.DiscardOldestPolicy());

    private final String policy;
    private final RejectedExecutionHandler handler;
}
