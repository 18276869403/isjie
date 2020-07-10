package org.jeecg.modules.demo.monitor.vo;


import lombok.Data;

@Data
public class CustomerDTOPassword {
    //用户名
    private String username;
    //密码
    private String password;
    //验证码
    private String code;
}
