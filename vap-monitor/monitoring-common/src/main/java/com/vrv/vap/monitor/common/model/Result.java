package com.vrv.vap.monitor.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class  Result<T> {
    private String code;
    private String msg;
    private T data;
}
