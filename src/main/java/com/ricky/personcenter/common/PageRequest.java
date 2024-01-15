package com.ricky.personcenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @author Ricky
 * @date 2024/01/15
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -870587200068780736L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 页码
     */
    protected int pageNum = 1;
}
