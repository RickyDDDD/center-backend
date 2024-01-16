package com.ricky.personcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricky.personcenter.model.dto.TeamqueryDTO;
import com.ricky.personcenter.model.entity.Team;
import com.ricky.personcenter.model.entity.User;
import com.ricky.personcenter.model.request.TeamJoinRequest;
import com.ricky.personcenter.model.request.TeamUpdateRequest;
import com.ricky.personcenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Ricky
* @description 针对表【team】的数据库操作Service
* @createDate 2024-01-15 11:19:47
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     *
     * @param team      团队
     * @param loginUser 登录用户
     * @return long
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamqueryDTO TeamQuery DTO
     * @param isAdmin      是管理员
     * @return {@link List}<{@link TeamUserVO}>
     */
    List<TeamUserVO> listTeams(TeamqueryDTO teamqueryDTO, boolean isAdmin);

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest 更新请求信息
     * @param loginUser         登录用户
     * @return boolean
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入团队
     *
     * @param teamJoinRequest 团队加入请求
     * @return boolean
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
