package com.ricky.personcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ricky.personcenter.common.ErrorCode;
import com.ricky.personcenter.error.BusinessException;
import com.ricky.personcenter.mapper.UserMapper;
import com.ricky.personcenter.model.entity.User;
import com.ricky.personcenter.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ricky.personcenter.contant.UserConstant.ADMIN_ROLE;
import static com.ricky.personcenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author Ricky
* &#064;description  针对表【user】的数据库操作Service实现
* &#064;createDate  2023-12-21 20:54:16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值
     */
    private static final  String SALT = "chenpi";



    /**
     * 用户注册
     *
     * @param userAccount   用户帐号
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @return int
     */
    @Override
    public int userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        //校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编码过长");
        }
        //账号不能存在特殊字符
        String validPattern = "^[a-zA-Z0-9 ]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号存在特殊字符");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.NULL_RESPONSE,"账号重复");
        }

        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.NULL_RESPONSE,"星球编号重复");
        }

        //2、密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        user.setUserStatus(0);
        user.setIsDelete(0);
        user.setUserRole(0);
        boolean saveResult = this.save(user);

        if (!saveResult){
            throw new BusinessException(ErrorCode.NULL_RESPONSE,"插入用户表数据错误");
        }
        return Math.toIntExact(user.getId());

    }

    /**
     * 做登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @return {@link User}
     */
    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        //账号不能存在特殊字符
        String validPattern = "^[a-zA-Z0-9 ]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号存在特殊字符");
        }

        //2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //检查用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount cannot be matched");
            throw new BusinessException(ErrorCode.NULL_RESPONSE);
        }
        //3、用户脱敏
        User safetyUser = getSafetyUser(user);

        //4、记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request 请求
     */
    @Override
    public int userLoginOut(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 按标签搜索用户(内存过滤版)
     *
     * @param tagNameList 标签列表
     * @return int
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //1、先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2、在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            //判断用户传进来的tagName是否存在
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map( this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 按 SQL 按标签搜索用户
     *
     * @param tagNameList 标签名称列表
     * @return {@link List}<{@link User}>
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }

        List<User> userList = userMapper.selectList(queryWrapper);

        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     *
     * @param user 用户
     * @return {@link Integer}
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员，允许更新所有用户信息
        //如果不是管理员，只允许跟新自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 获取已登录用户信息
     *
     * @param request 请求
     * @return {@link User}
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    /**
     * 判断是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
    @Override
    public boolean isAdmin(User loginUser){
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 用户脱敏
     *
     * @param originUser 源用户
     * @return {@link User}
     */
    @Override
    public User getSafetyUser( User originUser) {
        if (originUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入数据为空");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());
        return safetyUser;
    }

}




