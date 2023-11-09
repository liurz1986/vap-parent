package com.vrv.vap.toolkit.vo;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.ValidateTools.RetMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 分页返回实体
 */
@ApiModel("VList")
public class VList<T> extends Result {

    @ApiModelProperty("查询分页数据列表")
    private List<T> list;

    @ApiModelProperty("查询的数据总条数")
    private int total;

    public VList() {
    }

    public VList(int total, List<T> list) {
        this(total, list, RetMsgEnum.SUCCESS);
    }
    /**VList*/
    public VList(int total, List<T> list, RetMsgEnum rm) {
        super(rm);
        this.list = list;
        this.total = total;
    }
    /**VList*/
    public VList(int total, List<T> list, RetMsg rm) {
        super(rm);
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
