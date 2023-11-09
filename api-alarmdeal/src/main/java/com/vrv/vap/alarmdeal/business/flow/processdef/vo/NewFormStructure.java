package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewFormStructure {

    private  String type;

    private  String id;

    private  String itemType;

    private  String name; // 非工单2.0，表单中具体属性名称

    private  String icon;

    private OptionVO option;

    private List<NewFormStructure> children;



    @Data
    public static class   OptionVO{

        private String fieldId;

        private  String title;   // 工单2.0 表单总具体属性名称

        private  Object children;

        private  boolean  visible; // 工单2.0  是否隐藏 ，true表示隐藏


    }


}
