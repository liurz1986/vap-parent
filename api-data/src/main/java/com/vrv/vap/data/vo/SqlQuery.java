package com.vrv.vap.data.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;

@ApiModel(value = "标准SQL查询参数")
public class SqlQuery extends Query {

    @ApiModelProperty("表名")
    private String table;

    @ApiModelProperty("是否汇总，汇总时，将返回 Total (多进行一次查询)")
    private boolean total_;

    @ApiModelProperty("条件, 为JSON格式字符串，参考 WhereCondition")
    // 说明：不能直接用，需要实例化后进进行转换
    private String where;

    @ApiModelProperty("数据源id")
    private Integer sourceId;

    @ApiModelProperty("组件权限")
    private String moduleAuth;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isTotal_() {
        return total_;
    }

    public void setTotal_(boolean total_) {
        this.total_ = total_;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getModuleAuth() {
        return moduleAuth;
    }

    public void setModuleAuth(String moduleAuth) {
        this.moduleAuth = moduleAuth;
    }

    private String getOrderBy() {
        if (StringUtils.isNotBlank(this.getOrder_()) && StringUtils.isNotBlank(this.getBy_())) {
            return " ORDER BY " + this.getOrder_() + " " + this.getBy_() + " ";
        }
        return " ";
    }

    private String getLimit() {
        if (this.getCount_() > 0) {
            return "LIMIT " + this.getStart_() + " , " + this.getCount_();
        }
        return " ";
    }

    public String toSQL(String where) {
        return String.format("SELECT * FROM %s WHERE %s %s %s", this.table, where, this.getOrderBy(), this.getLimit());
    }


    public String toSqlNoLimit(String where) {
        return String.format("SELECT * FROM %s WHERE %s %s", this.table, where, this.getOrderBy());
    }

}
