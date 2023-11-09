package com.vrv.vap.data.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

@ApiModel(value = "标准SQL聚合计算字段")
public class SqlGroupField {

    @ApiModelProperty("字段名称")
    private String field;

    @ApiModelProperty("计算方法 ，count  sum min  等")
    private String calc;

    @ApiModelProperty("是否 distinct")
    private boolean distinct;

    @ApiModelProperty("别名")
    private String alias;

    public SqlGroupField() {
    }


    public SqlGroupField(String field, String calc, String alias, boolean distinct) {
        this.field = field;
        this.calc = calc;
        this.alias = alias;
        this.distinct = distinct;
    }

    public String getCalc() {
        return calc;
    }

    public void setCalc(String calc) {
        this.calc = calc;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        String alias = StringUtils.isBlank(this.alias) ? this.field : this.alias;

        if (this.distinct) {
            return String.format(" %s( DISTINCT `%s` ) AS %s ", this.calc, this.field, alias);
        }
        if ("COUNT".equalsIgnoreCase(this.calc)) {
            return String.format(" COUNT(0) AS %s ", alias);
        }
        return String.format(" %s( `%s` ) AS %s ", this.calc, this.field, alias);

    }
}
