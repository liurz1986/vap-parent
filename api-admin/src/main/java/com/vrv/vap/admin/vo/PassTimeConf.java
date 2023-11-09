package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.vo
 * @Author tongliang@VRV
 * @CreateTime 2019/03/11 15:43
 * @Description (密码复杂度及时效性配置的 VO )
 * @Version
 */
@Getter
@Setter
public class PassTimeConf {

    /**
     * @Description (允许登录失败的次数)
     */
	@ApiModelProperty("允许登录失败的次数")
    private Integer maxFailNumber;

    /**
     * @Description (登录失败被锁定时间 单位：分钟  默认值为30)
     */
	@ApiModelProperty("登录失败被锁定时间 单位：分钟  默认值为30")
    private Integer checkTimeSet = 30;

    /**
     * @Description (密码是否必须有大写字母)
     */
	@ApiModelProperty("密码是否必须有大写字母")
    private Short uppercase;

    /**
     * @Description (密码是否必须有小写字母)
     */
	@ApiModelProperty("密码是否必须有小写字母")
    private Short lowercase;

    /**
     * @Description (密码是否必须有特殊字符和下划线)
     */
	@ApiModelProperty("密码是否必须有特殊字符和下划线")
    private Short specialChart;

    /**
     * @Description (密码是否必须有数字)
     */
	@ApiModelProperty("密码是否必须有数字")
    private Short numbers;

    /**
     * @Description (密码最小长度)
     */
	@ApiModelProperty("密码最小长度")
    private Integer minlength = 8;

    /**
     * @Description (密码最大长度)
     */
	@ApiModelProperty("密码最大长度")
    private Integer maxlength = 20;

}
