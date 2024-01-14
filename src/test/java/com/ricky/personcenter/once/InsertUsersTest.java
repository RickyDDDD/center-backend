//package com.ricky.personcenter.once;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.ricky.personcenter.mapper.UserMapper;
//import com.ricky.personcenter.model.entity.User;
//import com.ricky.personcenter.service.UserService;
//import io.lettuce.core.protocol.CompleteableCommand;
//import org.apache.tomcat.util.threads.ThreadPoolExecutor;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.StopWatch;
//
//import javax.annotation.Resource;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class InsertUsersTest {
//
//    @Resource
//    private UserService userService;
//
//    @Resource
//    private UserMapper userMapper;
//
//    private ExecutorService executorService = new ThreadPoolExecutor(40,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
//
//    @Test
//    public void InsertUsers() throws Exception {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 100000;
//        List<User> userList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假陈皮");
//            user.setUserAccount("ricky");
//            user.setAvatarUrl("https://rickychen.oss-cn-shenzhen.aliyuncs.com/f494e011-f876-49c6-be71-b37d50fd7bec.jpg");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setPhone("12312");
//            user.setProfile("121221");
//            user.setEmail("112121");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setPlanetCode("3333");
//            user.setTags("[]");
//            userList.add(user);
//        }
//        //10万 10秒
//        userService.saveBatch(userList,10000);
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//
//
//    /**
//     * 执行并发插入用户
//     *
//     * @throws Exception 例外
//     */
//    @Test
//    public void doConcurrencyInsertUsers() throws Exception {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 100000;
//        int j = 0;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < 25; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假陈皮");
//                user.setUserAccount("ricky");
//                user.setAvatarUrl("https://rickychen.oss-cn-shenzhen.aliyuncs.com/f494e011-f876-49c6-be71-b37d50fd7bec.jpg");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("12312");
//                user.setProfile("121221");
//                user.setEmail("112121");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode("3333");
//                user.setTags("[]");
//                userList.add(user);
//                if (j % 20000 == 0) {
//                    break;
//                }
//            }
//            //异步执行
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println("threadName:" + Thread.currentThread().getName());
//                userService.saveBatch(userList, 10000);
//            }, executorService);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//
//}