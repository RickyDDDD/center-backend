package com.ricky.personcenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author Ricky
 * &#064;date  2023/12/22
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 7263995749531347630L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
