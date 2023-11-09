package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.threeInOne;

import lombok.Data;

/**
 * @author wudi
 * @date 2022/4/20 16:51
 */
@Data
public class ThreeInOneEventList {

    private String time; //时间
    private String remarks; //违规说明
    private String ip; //ip
    private String org; //组织结构

}
