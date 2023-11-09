package com.vrv.vap.toolkit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * es查询返回
 *
 * @author xw
 * @date 2018年5月3日
 */
@ApiModel("es 查询返回结果实体类")
public class EsResult extends Result {

    /**
     * 查询数据
     */
    @ApiModelProperty("查询数据")
    private List<Map<String, Object>> list;

    /**
     * 数据总量
     */
    @ApiModelProperty("数据总量")
    private long total;

    /**
     * 实际查询数据总量
     */
    @ApiModelProperty("实际查询数据总量")
    private long totalAcc;

    /**
     * 开始条数
     */
    @ApiModelProperty("开始条数")
    private int start;

    /**
     * 分页条数
     */
    @ApiModelProperty("分页条数")
    private int count;

    /**
     * scroll id
     */
    @ApiModelProperty("scroll id")
    private String scrollId;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalAcc() {
        return totalAcc;
    }

    public void setTotalAcc(long totalAcc) {
        this.totalAcc = totalAcc;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
