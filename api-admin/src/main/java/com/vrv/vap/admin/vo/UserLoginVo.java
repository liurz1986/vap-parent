package com.vrv.vap.admin.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.vo
 * @Author tongliang@VRV
 * @CreateTime 2019/03/19 15:35
 * @Description (用户登录vo)
 * @Version
 */
@Setter
@Getter
public class UserLoginVo {

    private String uuu;

    private String ppp;

    private String bpp;

    private String mmm;

    private String verCode;

    @Override
    public String toString() {
        return "UserLoginVo{" +
                "uuu='" + uuu + '\'' +
                ", ppp='" + ppp + '\'' +
                ", bpp='" + bpp + '\'' +
                ", mmm='" + mmm + '\'' +
                ", verCode='" + verCode + '\'' +
                '}';
    }
}
