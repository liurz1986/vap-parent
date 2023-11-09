package com.vrv.vap.alarmdeal.business.buinesssystem.vo;

import lombok.Data;

import java.util.List;

/**
 * 业务系统
 * @author vrv
 *
 */

@Data
public class BuinessSystemSaveVO extends  BuinessSystemVO{

    private List<SysdomainAssetVO> assets; // 资产关联信息
}