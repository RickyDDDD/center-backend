package com.ricky.personcenter.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricky.personcenter.common.ErrorCode;
import com.ricky.personcenter.error.BusinessException;
import com.ricky.personcenter.mapper.TeamMapper;
import com.ricky.personcenter.model.dto.TeamqueryDTO;
import com.ricky.personcenter.model.entity.Team;
import com.ricky.personcenter.model.entity.User;
import com.ricky.personcenter.model.entity.UserTeam;
import com.ricky.personcenter.model.enums.TeamStatusEnum;
import com.ricky.personcenter.model.request.TeamJoinRequest;
import com.ricky.personcenter.model.request.TeamUpdateRequest;
import com.ricky.personcenter.model.vo.TeamUserVO;
import com.ricky.personcenter.model.vo.UserVO;
import com.ricky.personcenter.service.TeamService;
import com.ricky.personcenter.service.UserService;
import com.ricky.personcenter.service.UserTeamService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author Ricky
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-01-15 11:19:47
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    /**
     * 创建团队
     *
     * @param team      团队
     * @param loginUser 登录用户
     * @return long
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1、判断请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2、是否登录，未登录不允许建队伍
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final Long userId = loginUser.getId();
        //3、校验信息
        //1.队伍人数 >1 && <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //2.队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        //3.队伍描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        //4.status 是否公开，不公开默认0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足");
        }
        //5.如果 status 是加密状态，一定要有密码，并且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
        }
        //6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }

        //7.校验用户最多只能创建 5 个队伍
        //todo 存在bug，可能用户疯狂点击创建好多个队伍-->解决方法：加锁/分布式锁
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多只能创建5个队伍");
        }
        //8、插入队伍信息到队伍表中
        //long nextId = IdUtil.getSnowflakeNextId();
        team.setId(null);
        team.setUserId(userId);
        team.setCreateTime(new DateTime());
        team.setUpdateTime(new DateTime());
        boolean result = this.save(team);
        if (!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        //9 插入用户 队伍关系和关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        Long teamId = team.getId();
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    /**
     * 列出队伍
     *
     * @param teamqueryDTO TeamQuery DTO
     * @param isAdmin      是管理员
     * @return {@link List}<{@link TeamUserVO}>
     */
    @Override
    public List<TeamUserVO> listTeams(TeamqueryDTO teamqueryDTO, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamqueryDTO != null) {
            Long id = teamqueryDTO.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id",id);
            }
            String searchText = teamqueryDTO.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description", searchText));
            }
            Long userId = teamqueryDTO.getUserId();
            if (userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            Integer status = teamqueryDTO.getStatus();
            if (status != null && status > -1){
                queryWrapper.eq("status", status);
            }
            String name = teamqueryDTO.getName();
            if (StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamqueryDTO.getDescription();
            if (StringUtils.isNotBlank(description)){
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamqueryDTO.getMaxNum();
            if (maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
        }
        //不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        //根据状态来查询
        Integer status = teamqueryDTO.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status",statusEnum.getValue());

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        //todo 关联查询已加入队伍的用户信息（可能会耗费性能，建议用自己写sql的方式实现）
        return teamUserVOList;
    }

    /**
     * 修改队伍信息
     *
     * @param teamUpdateRequest 更改队伍信息请求
     * @param loginUser         登录用户
     * @return boolean
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询队伍是否存在
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 只有队伍创建者和管理员才能修改队伍信息
        if (!oldTeam.getUserId().equals(loginUser.getId()) || !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //校对修改值和原始数据是否一致，一致的话提示不用修改
        // TeamName
        if (oldTeam.getName().equals(teamUpdateRequest.getName())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的内容相同");
        }
        // description
        if (oldTeam.getDescription().equals(teamUpdateRequest.getDescription())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的内容相同");
        }
        // exporetime
        if (oldTeam.getExpireTime().equals(teamUpdateRequest.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的内容相同");
        }
        // status
        if (oldTeam.getStatus().equals(teamUpdateRequest.getStatus())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的内容相同");
        }
        // password
        if (oldTeam.getPassword().equals(teamUpdateRequest.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的内容相同");
        }

        //如果队伍状态为加密，必须要有密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET) && (StringUtils.isBlank(teamUpdateRequest.getPassword()))){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须要设置密码");
        }
        if (statusEnum.equals(TeamStatusEnum.PUBLIC) && (StringUtils.isNotBlank(teamUpdateRequest.getPassword()))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非加密房间不需要设置密码");
        }
        if (statusEnum.equals(TeamStatusEnum.PRIVATE) && (StringUtils.isNotBlank(teamUpdateRequest.getPassword()))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非加密房间不需要设置密码");
        }

        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入团队
     *
     * @param teamJoinRequest 团队加入请求
     * @return boolean
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null ){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH,"禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
            }
        }

        //该用户已加入的队伍数量
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }

        //todo 还是可能存在重复添加的情况：用户疯狂点击的情况下；解决：需要加锁
        //不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userId);
        userTeamQueryWrapper.eq("teamId",teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入已加入的队伍");
        }

        //已加入队伍的人数
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq(" teamId",teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (teamHasJoinNum >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }

        //修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }
}






