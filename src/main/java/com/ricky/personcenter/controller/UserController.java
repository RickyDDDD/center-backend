package com.ricky.personcenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricky.personcenter.common.BaseResponse;
import com.ricky.personcenter.common.ErrorCode;
import com.ricky.personcenter.common.ResultUtils;
import com.ricky.personcenter.error.BusinessException;
import com.ricky.personcenter.model.entity.User;
import com.ricky.personcenter.model.request.UserLoginRequest;
import com.ricky.personcenter.model.request.UserRegisterRequest;
import com.ricky.personcenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ricky.personcenter.contant.UserConstant.*;

/**
 * 用户控制器
 *
 * @author Ricky
 * @date 2023/12/22
 */
@Slf4j
@RestController
//解决请求跨域问题，限制前端请求地址（http://localhost:5173/）
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return {@link Long}
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long result = (long) userService.userRegister(userAccount, userPassword, checkPassword, planetCode);

        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return {@link User}
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest , HttpServletRequest request){
        log.info("用户登录，登录信息{}",userLoginRequest);
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link Integer}>
     */
    @PostMapping("/loginout")
    public BaseResponse<Integer> userLoginOut(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.userLoginOut(request);
        return ResultUtils.success(result);
    }

    /**
     * 搜索用户
     *
     * @param username 用户名
     * @param request  请求
     * @return {@link List}<{@link User}>
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    /**
     * 主页的推荐用户
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("percachejob:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        //无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        //写缓存
        try {
            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.info("redis set key error", e);
        }
        return ResultUtils.success(userPage);
    }


    /**
     * 按标签搜索用户
     *
     * @param tagNameList 标签名称列表
     * @return {@link BaseResponse}<{@link List}<{@link User}>>
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    /**
     * 删除用户
     *
     * @param id      编号
     * @param request 请求
     * @return boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id , HttpServletRequest request){
        //仅管理员才能查询
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 获取当前用户
     *
     * @param request 请求
     * @return {@link User}
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        //TODO: 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 修改用户信息
     *
     * @param user    用户
     * @param request 请求
     * @return {@link BaseResponse}<{@link Integer}>
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user , HttpServletRequest request){
        //1. 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }


}
