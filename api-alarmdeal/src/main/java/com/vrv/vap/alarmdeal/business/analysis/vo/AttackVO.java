package com.vrv.vap.alarmdeal.business.analysis.vo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AttackPathVO;
import lombok.Data;

import java.util.List;

@Data
public class AttackVO {

    private List<AttackNodeVO> srcIps;  //源IP集合
    private List<AttackNodeVO> dstIps;  //目的IP集合
    private List<AttackPathVO> attackPath; //攻击路径
}
