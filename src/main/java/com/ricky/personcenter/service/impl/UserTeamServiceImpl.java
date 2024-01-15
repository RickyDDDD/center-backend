package com.ricky.personcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.personcenter.mapper.UserTeamMapper;
import com.ricky.personcenter.model.entity.UserTeam;
import com.ricky.personcenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author Ricky
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-01-15 11:19:47
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




