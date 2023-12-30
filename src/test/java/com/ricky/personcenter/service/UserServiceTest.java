package com.ricky.personcenter.service;
import java.util.Date;

import com.ricky.personcenter.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("chenpi");
        user.setAvatarUrl("https://rickychen.oss-cn-shenzhen.aliyuncs.com/f494e011-f876-49c6-be71-b37d50fd7bec.jpg");
        user.setGender(0);
        user.setUserPassword("");
        user.setPhone("1111111");
        user.setEmail("21212121");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "chenpi";
        String userPassword = "1111111111";
        String checkPassword = "1111111111";
        String planetCode = "121211";
        int result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        //
        // userAccount = "chenpi";
        // userPassword = "12345";
        // checkPassword = "123456";
        //result = userService.userRegister(userAccount, userPassword, checkPassword);
        //Assertions.assertEquals(-1,result);
    }
}