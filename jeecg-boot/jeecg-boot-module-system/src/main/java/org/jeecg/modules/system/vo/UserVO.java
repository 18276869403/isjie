package org.jeecg.modules.system.vo;

import lombok.Data;

/**
 * @program: jeecg-boot-parent
 * @description: 重置密码
 * @author: llm
 * @create: 2020-03-02 17:42
 **/
@Data
public class UserVO {
    private String id;
    private String username;
    private String oldPassword;
    private String newPassword;
}
