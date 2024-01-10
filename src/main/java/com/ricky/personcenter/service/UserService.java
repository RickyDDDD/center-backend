package com.ricky.personcenter.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ricky.personcenter.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Ricky
* @description 针对表【user】的数据库操作Service
* @createDate 2023-12-21 20:54:16
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @return int
     */
    int userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 做登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @return {@link User}
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     */
    int userLoginOut(HttpServletRequest request);

    /**
     * 按标签搜索用户
     *
     * @param tagNameList 标签列表
     * @return int
     */
    List<User> searchUserByTags(List<String> tagNameList);

    User getSafetyUser(User originUser);
}
