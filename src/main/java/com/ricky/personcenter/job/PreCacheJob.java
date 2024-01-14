package com.ricky.personcenter.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricky.personcenter.model.entity.User;
import com.ricky.personcenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务,通过redisson实现分布式锁
 *
 * @author Ricky
 * @date 2024/01/14
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天执行，预热推荐用户
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("personcenter:precache:docache:lock");
        try {
            //只有一个线程能获取到锁
            if ( lock.tryLock(0,-1,TimeUnit.MILLISECONDS) ) {
                System.out.println("getLock:" + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("percachejob:user:recommend:%s", userId);
                    ValueOperations<String,Object> operations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        operations.set(redisKey,userPage,30000, TimeUnit.MICROSECONDS);
                    } catch (Exception e) {
                        log.info("redis key error: " + e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.info("Interrupted exception occurred" , e);
        } finally {
            //判断这个锁是否是当前线程，只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
                System.out.println("unlock:" + Thread.currentThread().getId());
            }
        }
    }
}
