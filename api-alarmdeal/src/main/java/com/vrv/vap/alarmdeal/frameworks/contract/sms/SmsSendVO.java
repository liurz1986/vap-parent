package com.vrv.vap.alarmdeal.frameworks.contract.sms;

import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseCommonVo;
import lombok.Data;

import java.util.Map;

@Data
public class SmsSendVO extends ResponseCommonVo {

    private String id; //主键
    private Map<String,Object> recipient; //接收人
    private String content; //文件内容
}
