package com.vrv.vap.data.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;


@ApiModel(value = "标准SQL聚合参数")
public class SqlGroup extends Query {

    @ApiModelProperty("表名")
    private String table;

    @ApiModelProperty("需要计算的字段")
    private SqlGroupField[] fields;

    @ApiModelProperty("条件, 为JSON格式字符串，参考 WhereCondition")
    private String where;

    @ApiModelProperty("数据源id")
    private Integer sourceId;

    @ApiModelProperty("组件权限")
    private String moduleAuth;


    @ApiModelProperty("说明: 在Group查询中， Order 为需要 Group 的字段")
    private String order_;

    @ApiModelProperty("说明: 在Group查询中， By 为 Group 的计算方式，默认不计算, 支持 （时间: m/分钟 h/小时 d/天 M 月） (数字 : interval-{number}/ 区间段) ")
    private String by_;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public SqlGroupField[] getFields() {
        return fields;
    }

    public void setFields(SqlGroupField[] fields) {
        this.fields = fields;
    }

    public void setField(SqlGroupField field) {
        this.fields = new SqlGroupField[]{field};
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

    private String dateHistogram = null;

    private String histogram = null;

    private boolean calcGroup() {
        if (this.getBy_() == null || this.getBy_().length() == 0) {
            return false;
        }
        if (this.getBy_().startsWith("histogram-")) {
            this.histogram = this.getBy_().substring(10);
            return true;
        }
        if (this.getBy_().startsWith("datetrend-")) {
            String interval = this.getBy_().substring(10);
            switch (interval) {
                case "m":
                case "1m":
                    this.dateHistogram = String.format("DATE_FORMAT(%s,'%s')", this.getOrder_(), "%Y-%m-%d %H:%i:00");
                    return true;
                case "h":
                case "1h":
                    this.dateHistogram = String.format("DATE_FORMAT(%s,'%s')", this.getOrder_(), "%Y-%m-%d %H:00:00");
                    return true;
                case "d":
                case "1d":
                    this.dateHistogram = String.format("DATE_FORMAT(%s,'%s')", this.getOrder_(), "%Y-%m-%d 00:00:00");
                    return true;
                case "M":
                case "1M":
                    this.dateHistogram = String.format("DATE_FORMAT(%s,'%s')", this.getOrder_(), "%Y-%m-01 00:00:00");
                    return true;
            }
        }
        if (this.getBy_().equals("24hours")) {
            this.dateHistogram = String.format("DATE_FORMAT(%s,'%s')", this.getOrder_(), "%H");
            return true;
        }


        return false;

    }

    private String calcFields() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(this.getOrder_())) {
            sb.append(',');
        }
        if (this.fields == null || this.fields.length == 0) {
            sb.append(" COUNT(0) as doc_count,");
        } else {
            for (SqlGroupField field : this.fields) {
                sb.append(field.toString());
                sb.append(',');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    public String toSQL(String where) {
        // 计量
        String whereStr = StringUtils.isBlank(where) ? "" : (" AND " + where);
        if (StringUtils.isBlank(this.getOrder_())) {
            return this.toMetric(whereStr);
        }
        boolean isCustom = this.calcGroup();
        if (isCustom) {
            // 自定义 - 日期直方图
            if (this.dateHistogram != null) {
                return this.toDateHistogram(whereStr);
            }
            // 自定义 - 直方图
            if (this.histogram != null) {
                return this.toHistogram(whereStr);
            }
        }
        String order;
        if (this.fields != null && this.fields.length > 0) {
            order = this.fields[0].getAlias() != null ? this.fields[0].getAlias() : this.fields[0].getField();
        } else {
            order = "doc_count";
        }
        // 普通Group
        return String.format("SELECT `%s` AS `key` %s FROM %s WHERE NOT ISNULL(`%s`)  %s GROUP BY %s  ORDER BY `%s` DESC LIMIT %d,%d",
                this.getOrder_(), this.calcFields(), this.table,  //SELECT
                this.getOrder_(), whereStr,        // WHERE
                this.getOrder_(),                  // GROUP
                order,                  // ORDER
                this.getStart_(), this.getCount_());    // LIMIT
    }

    private String toMetric(String whereStr) {
        // 仅计算,用于计算一些指标时，order_ 传 null
        return String.format("SELECT %s FROM %s WHERE 1=1  %s  LIMIT 0,1",
                this.calcFields(), this.table,  //SELECT
                whereStr
        );
    }

    private String toDateHistogram(String where) {
        // 日期直方图
        return String.format("SELECT %s AS `key` %s FROM %s WHERE NOT ISNULL(%s)  %s GROUP BY %s ORDER BY `key` ASC",
                dateHistogram, this.calcFields(), this.table,  //SELECT
                dateHistogram, where,      // WHERE
                dateHistogram              // GROUP
        );
    }


    private String toHistogram(String where) {
        String order = this.getOrder_();
        // 直方图
        return String.format("SELECT FLOOR(%s/%s)*%s as `key`, %s as _interval %s FROM %s WHERE NOT ISNULL(%s)  %s GROUP BY FLOOR(%s/%s)  ORDER BY `key` ASC",
                order, this.histogram, this.histogram, this.histogram,
                this.calcFields(), this.table,  //SELECT
                order, where,
                order, this.histogram

        );
    }


}
