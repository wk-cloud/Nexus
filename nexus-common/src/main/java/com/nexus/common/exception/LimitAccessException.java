package com.nexus.common.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限制访问异常
 *
 * @author wk
 * @date 2023/3/29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LimitAccessException extends RuntimeException{

    private static final long serialVersionUID = 2258477558314498008L;

    private String message;
}
