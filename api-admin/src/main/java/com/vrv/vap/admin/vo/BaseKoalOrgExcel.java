package com.vrv.vap.admin.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class BaseKoalOrgExcel {
    /**
     * 机构编码
     */
    @Excel(name = "机构编码" ,orderNum = "1")
    private String code;

    /**
     * 上级机构编码
     */
    @Excel(name = "上级机构编码" ,orderNum = "2")
    private String parentCode;

    /**
     * 机构名称
     */
    @Excel(name = "机构名称" ,orderNum = "3")
    private String name;

    /**
     * 机构类型
     */
    @Excel(name = "机构类型" ,orderNum = "4")
    private String type;

    /**
     * 保密等级 0绝密，1机密，2秘密，3内部
     */
    @Excel(name = "保密等级" ,orderNum = "5")
    private String secretLevel;

    /**
     *  防护等级 1秘密，2机密，3绝密（增强），4绝密
     */
    @Excel(name = "防护等级" ,orderNum = "6")
    private String protectionLevel;

    /**
     * 保密资格：1-JG一级；2-JG二级
     */
    @Excel(name = "保密资格" ,orderNum = "7")
    private String secretQualifications;

    /**
     *  单位类别 1-行政机关、2-事业单位；3-国有企业；4-中央企业
     */
    @Excel(name = "单位类别" ,orderNum = "8")
    private String orgType;

    @Excel(name = "IP范围",orderNum = "9")
    private String ipRanges;

    /**
     * 异常原因
     */
    private String reason;
}