package com.ricky.personcenter.service;
import java.util.Date;

import com.ricky.personcenter.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //添加数据
        User user = new User();
        user.setId(1L);
        user.setUsername("chenpi");
        valueOperations.set("chenpiString","dog");
        valueOperations.set("chenpiInt",1);
        valueOperations.set("chenpiDouble",2.0);
        valueOperations.set("chenpiUser",user);

        //查询数据
        Object chenpi = valueOperations.get("chenpiString");
        Assertions.assertTrue("dog".equals((String)chenpi));
        chenpi = valueOperations.get("chenpiInt");
        Assertions.assertTrue(1 == (Integer) chenpi);
        chenpi = valueOperations.get("chenpiDouble");
        Assertions.assertTrue(2.0 == (Double) chenpi);
        System.out.println(valueOperations.get("chenpiUser"));


    }

}
