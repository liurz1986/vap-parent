package com.vrv.vap.alarmdeal.business.baseauth.vo.query;

import com.vrv.vap.jpa.web.page.PageReqVap;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lps 2021/8/10
 */

@Data
public class BaseAuthPrintBurnQueryVo extends PageReqVap {
    private  Integer id;

    /**
     * ip
     */
    private String ip;

    /**
     * 人员姓名
     */
    private String responsibleName;
    private Integer decide;
    private Integer type;//1打印 2刻录
    private List<String> ips=new ArrayList<>();
    private List<Integer> ids=new ArrayList<>();
}
