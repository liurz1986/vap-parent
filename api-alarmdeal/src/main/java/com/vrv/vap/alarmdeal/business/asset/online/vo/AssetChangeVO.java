package com.vrv.vap.alarmdeal.business.asset.online.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资产变更VO
 */
@Data
public class AssetChangeVO {

    private String guid ; //主键

    private String ip;// 资产ip

    private String assetTypeName;// 台账资产类型(资产表中二级资产类型名称)

    private String scanTypeName;// 发现资产类型(发现的资产小类名称)

    private String status; //状态

    private String handleStatus; //处理状态  处理状态："0"表示已经处理

    private String handleUserName; //处理人名称

    private String handleUserId; //处理人id
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime; //处理时间

    private String opinion;//处理意见
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; //时间

}
