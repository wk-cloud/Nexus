package com.nexus.common.core.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 限制访问异常
 *
 * @author wk
 * @date 2023/3/29
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LimitAccessException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 2258477558314498008L;

    private String message;
}
