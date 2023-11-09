package com.vrv.vap.alarmdeal.business.appsys.datasync.vo;

import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import lombok.Data;


@Data
public class AppValidateVO {
    private boolean isExistData; // 是否存在数据(针对主表)

    private AppSysManagerVerify appSysManagerVerify; // 待审表对象

    private AppSysManager appSysManager;  // 主表对象

    private boolean checkSucess; // 校验是否成功

    private Integer appId;// 主表的id

}
