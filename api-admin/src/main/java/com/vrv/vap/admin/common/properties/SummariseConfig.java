package com.vrv.vap.admin.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lilang
 * @date 2020/7/21
 * @description
 */
@Component
@ConfigurationProperties(prefix = "summarise")
public class SummariseConfig {

    private Integer total;

    private Integer refresh;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRefresh() {
        return refresh;
    }

    public void setRefresh(Integer refresh) {
        this.refresh = refresh;
    }
}
