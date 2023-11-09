package com.vrv.vap.xc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommunicationModel extends PageModel{
    private String name;
    private String rangeIps;
    private double total;
}
