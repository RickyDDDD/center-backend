package com.ricky.personcenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 团队添加 DTO
 *
 * @author Ricky
 * @date 2024/01/15
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 4806794963733196042L;

    /**
     * 编号
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 队伍密码
     */
    private String password;
}
