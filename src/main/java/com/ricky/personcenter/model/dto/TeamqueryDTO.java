package com.ricky.personcenter.model.dto;

import com.ricky.personcenter.common.PageRequest;
import lombok.Data;

/**
 * TeamQuery DTO
 *
 * @author Ricky
 * @date 2024/01/15
 */
@Data
public class TeamqueryDTO  extends PageRequest {

    private Long id;

    private String name;

    private String description;

    private Integer maxNum;

    private Long userId;

    private Integer status;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

}
