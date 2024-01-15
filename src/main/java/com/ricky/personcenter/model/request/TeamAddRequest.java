package com.ricky.personcenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 团队添加 DTO
 *
 * @author Ricky
 * @date 2024/01/15
 */
@Data
public class TeamAddRequest implements Serializable {
    private static final long serialVersionUID = -7085568236829031695L;

    private String name;

    private String description;

    private Integer maxNum;

    private Data expireTime;

    private Long userId;

    private Integer status;

    private String password;
}
