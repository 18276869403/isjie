package org.jeecg.modules.system.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneCodeVO implements Serializable {
    private String username;
    private String password;
    private String code;
}