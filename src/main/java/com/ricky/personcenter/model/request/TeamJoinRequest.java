package com.ricky.personcenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 团队加入请求
 *
 * @author Ricky
 * @date 2024/01/16
 */
@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 1671080399928056710L;

    private Long teamId;

    private String password;

}
