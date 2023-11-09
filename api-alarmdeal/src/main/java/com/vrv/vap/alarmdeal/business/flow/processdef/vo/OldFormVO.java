package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

@Data
public class OldFormVO {

    private String key;

    private ContextVO context;

    @Data
    public static class ContextVO {

        private  String type;   //组件类型

        private  String field;  //组件中文名称

        private  String code;  //与key对应

        private  String cascadeName; //联动分类

    }











}
