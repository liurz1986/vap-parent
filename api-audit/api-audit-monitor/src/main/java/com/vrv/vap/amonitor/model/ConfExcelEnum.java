package com.vrv.vap.amonitor.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-07-12
 */
@ApiModel(value="ConfExcelEnum对象", description="")
public class ConfExcelEnum {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty(value = "模板编码")
    private String excelCode;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "字段信息 [{column:英文字段名,format:格式化处理方法}]")
    private String columnInfo;

    @ApiModelProperty(value = "exce列名称")
    private String excelCol;

    @ApiModelProperty(value = "0导出 1导入")
    private Integer excelType;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "描述")
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
    public String getExcelCol() {
        return excelCol;
    }

    public void setExcelCol(String excelCol) {
        this.excelCol = excelCol;
    }
    public Integer getExcelType() {
        return excelType;
    }

    public void setExcelType(Integer excelType) {
        this.excelType = excelType;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
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
            ", excelCol=" + excelCol +
            ", excelType=" + excelType +
            ", createTime=" + createTime +
            ", description=" + description +
        "}";
    }
}
