package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.TerminalAssteInstallTime;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 *
 *  处理系统安装时间对象
 *
 */
@Data
public class TerminalAssteInstallTimeJobVO  {

    private Asset asset;

    private Date oldOsSetupTime; // 历史安装时间

    private String type; // 1.新增 2.修改 3.删除 4.全量

    private List<String> guids ; // 多个资产guid ，目前用在删除

}
