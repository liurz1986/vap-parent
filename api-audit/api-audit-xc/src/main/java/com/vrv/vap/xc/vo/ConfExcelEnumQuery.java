package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="ConfExcelEnum对象", description="")
public class ConfExcelEnumQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "模板编码")
    private String excelCode;

    @ApiModelProperty(value = "文件名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String fileName;

    @ApiModelProperty(value = "字段信息 [{columnEn:英文字段名,columnCn:中文字段名,size:长度限制,isNull:能否为空:0是1否}]")
    private String columnInfo;

    @ApiModelProperty(value = "0导出 1导入")
    private Integer excelType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getExcelCode() {
        return excelCode;
    }

    public void setExcelCode(String excelCode) {
        this.excelCode = excelCode;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(String columnInfo) {
        this.columnInfo = columnInfo;
    }
    public Integer getExcelType() {
        return excelType;
    }

    public void setExcelType(Integer excelType) {
        this.excelType = excelType;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ConfExcelEnum{" +
            "id=" + id +
            ", excelCode=" + excelCode +
            ", fileName=" + fileName +
            ", columnInfo=" + columnInfo +
            ", excelType=" + excelType +
            ", createTime=" + createTime +
            ", description=" + description +
        "}";
    }
}
