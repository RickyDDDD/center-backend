package com.ricky.personcenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ricky
 */
@Data
@EqualsAndHashCode
public class XingqiuTableUserInfo {

    @ExcelProperty("成员编号")
    private String planetCode;

    @ExcelProperty("成员昵称")
    private String username;

}