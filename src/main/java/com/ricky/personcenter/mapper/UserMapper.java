package com.ricky.personcenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ricky.personcenter.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Ricky
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-12-21 20:54:16
* @Entity mybatisx.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




