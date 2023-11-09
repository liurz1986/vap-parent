package com.vrv.vap.alarmdeal.business.evaluation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 总共产生推荐自查自评项XX项，已自查自评XX项，推荐的自查自评项中涉及问题部门 XX个，涉及XX个检查大类，XX个成因类型；涉及监管事件合计XXX条
 */
@Data
public class SummaryVO {

    /**
     * 共产生推荐自查自评项
     */
    private int totalCount = 0;

    /**
     * 已自查自评项
     */
    private int finshCount = 0;

    /**
     * 涉及问题部门个数
     */
    private int depCount = 0;
    /**
     * 检查大类个数
     */
    private int checkTypeCount = 0;

    /**
     * 成因类型个数
     */
    private int geneticTypeCount = 0;
    /**
     * 涉及监管事件合计条数
     */
    private int eventCount = 0;

    // 开始时间
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    // 结束时间
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;


}
