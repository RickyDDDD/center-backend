package com.ricky.personcenter.once;


import com.ricky.personcenter.mapper.UserMapper;
import com.ricky.personcenter.model.entity.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
    //@Scheduled(fixedDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假陈皮");
            user.setUserAccount("ricky");
            user.setAvatarUrl("https://rickychen.oss-cn-shenzhen.aliyuncs.com/f494e011-f876-49c6-be71-b37d50fd7bec.jpg");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("12312");
            user.setProfile("121221");
            user.setEmail("112121");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("3333");
            user.setTags("[]");

            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

}
