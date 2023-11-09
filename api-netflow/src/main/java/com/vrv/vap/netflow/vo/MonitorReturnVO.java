package com.vrv.vap.netflow.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wh1107066
 * @date 2023/8/25
 */
@Builder
@Data
@NoArgsConstructor
public class MonitorReturnVO implements Serializable {
    private Integer type;
    private String message;
    public MonitorReturnVO(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
