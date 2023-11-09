package com.vrv.vap.xc.model;

import com.vrv.vap.toolkit.constant.FileDirEnum;
import lombok.Data;

@Data
public class NetWorkCaseModel {
    private String name;
    private int accessNum;
    private int downloadNum;
    private int uploadNum;
}
