package com.ricky.personcenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        //list，数据存在本地jvm内存中
        List<String> list = new ArrayList<>();
        //list.add("chenpi");
        //System.out.println("list:" + list.get(0));

        list.remove(list);

        //数据存在redis的内存中
        RList<String> rList = redissonClient.getList("test_list");//这里相当于设置了key-value中的key值
        //rList.add("chenpi");
        //System.out.println("rlist:" + rList.get(0));

        rList.remove(0);

        //map



        //set


    }

}
