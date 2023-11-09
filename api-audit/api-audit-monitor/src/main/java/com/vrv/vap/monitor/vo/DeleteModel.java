package com.vrv.vap.monitor.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel("删除构造体")
public class DeleteModel {
    @ApiModelProperty("主键删除数据,多个用','分割")
    @TableField(exist = false)
    private String ids;

    /**
     * 用于补充日志信息
     */
    private String extendDesc;

    public Integer getIntegerId() {
        return Integer.parseInt(ids.split(",")[0]);
    }

    public String getSingleId() {
        return ids.split(",")[0];
    }

    public List<String> getStringIdList() {
        return Arrays.asList(ids.split(","));
    }

    public List<Integer> getIntegerIdList() {
        return getStringIdList().stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getExtendDesc() {
        return extendDesc;
    }

    public void setExtendDesc(String extendDesc) {
        this.extendDesc = extendDesc;
    }
}
