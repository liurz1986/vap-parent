package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.Map;

@Data
public class TransferFlowByCallBackVo {


    private  String ticketId;

    private String userId;

    private  String action;

    private Map<String,Object> busiarg;

}
