package com.ricky.personcenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ricky.personcenter.common.BaseResponse;
import com.ricky.personcenter.common.ErrorCode;
import com.ricky.personcenter.common.PageRequest;
import com.ricky.personcenter.common.ResultUtils;
import com.ricky.personcenter.error.BusinessException;
import com.ricky.personcenter.model.dto.TeamqueryDTO;
import com.ricky.personcenter.model.entity.Team;
import com.ricky.personcenter.service.TeamService;
import com.ricky.personcenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
 import java.util.List;

/**
 * 组队接口
 *
 * @author Ricky
 * @date 2024/01/15
 */
@RestController
@Slf4j
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173/"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    /**
     * 添加团队
     *
     * @param team 团队
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean save = teamService.save(team);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }
        return ResultUtils.success(team.getId());
    }

    /**
     * 删除团队
     *
     * @param id 编号
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 修改队伍信息
     *
     * @param team 团队
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 按 ID 获取团队
     *
     * @param id 编号
     * @return {@link BaseResponse}<{@link Team}>
     */
    @PostMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表
     *
     * @param teamqueryDTO TeamQuery DTO
     * @return {@link BaseResponse}<{@link List}<{@link Team}>>
     */
    public BaseResponse<List<Team>> listTeams(TeamqueryDTO teamqueryDTO){
        if (teamqueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team, teamqueryDTO);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }

    /**
     * 按页面列出队伍信息
     *
     * @param teamqueryDTO TeamQuery DTO
     * @return {@link BaseResponse}<{@link Page}<{@link Team}>>
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamqueryDTO teamqueryDTO){
        if (teamqueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team, teamqueryDTO);
        Page<Team> page = new Page<>(teamqueryDTO.getPageNum(), teamqueryDTO.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

}
