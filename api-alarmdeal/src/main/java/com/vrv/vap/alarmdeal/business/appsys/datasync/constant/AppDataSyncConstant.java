package com.vrv.vap.alarmdeal.business.appsys.datasync.constant;

public class AppDataSyncConstant {

    /**
     * 1、待编辑；2、待入库、3、入库失败 ;4、已入库(入库成功);5、已忽略
     */
    public final static int SYNCSTATUSEDIT=1; //  待编辑

    public final static int SYNCSTATUSWAIT=2; // 待入库

    public final static int SYNCSTATUSFAIL=3; // 入库失败

    public final static int SYNCSTATUSSUCCESS=4; // 已入库(入库成功)

    public final static int SYNCSTATUSNEG=5; //已忽略


}
