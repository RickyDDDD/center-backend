package com.ricky.personcenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 7263995749531347630L;
    private String userAccount;
    private String userPassword;
}
