package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import com.vrv.vap.alarmdeal.business.analysis.vo.AttackNodeVO;
import lombok.Data;

@Data
public class AttackPathVO {

    private AttackNodeVO srcAsset;  //源IP集合
    private AttackNodeVO dstAsset;  //目的IP集合
}
