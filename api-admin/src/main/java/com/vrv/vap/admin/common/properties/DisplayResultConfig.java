package com.vrv.vap.admin.common.properties;

import com.vrv.vap.admin.common.config.Frozen;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author lilang
 * @date 2020/4/26
 * @description 搜索结果显示按钮及冻结列配置
 */
@Component
@ConfigurationProperties(prefix = "display")
public class DisplayResultConfig {

    //按钮顺序
    private String order;

    @NestedConfigurationProperty
    private Frozen frozen = new Frozen();

    private int days;


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Frozen getFrozen() {
        return frozen;
    }

    public void setFrozen(Frozen frozen) {
        this.frozen = frozen;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
