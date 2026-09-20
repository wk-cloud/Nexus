package com.nexus.test;

import com.nexus.common.core.annotation.Pass;
import com.nexus.common.redis.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RedissonTestController
 *
 * @author wk
 * @date 2026/4/26 17:11
 */
@RestController
@RequestMapping("/redisson")
public class RedissonTestController {

    @Resource
    private Redisson redisson;

    @GetMapping("/test")
    @Pass
    public String test() {
        RLock redissonLock = redisson.getLock("test");
        redissonLock.lock();
        try {
            Boolean test = RedisUtils.hasKey("test");
            return "测试成功" + redissonLock.isLocked();
        }finally {
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
     }
}
