package com.vrv.vap.data.model;

import com.vrv.vap.data.constant.SYSTEM;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_maintain")
@ApiModel(value = "配置维护")
public class Maintain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("实体命名")
    private String name;

    @Column(name = "source_id")
    @ApiModelProperty("数据源ID，仅支持内置MYSQL")
    private Integer sourceId;

    @Column(name = "primary_key")
    @ApiModelProperty("主键，必须有，没有不能CRUD")
    private String primaryKey;

    @Column(name = "name_field")
    @ApiModelProperty("名称字段")
    private String nameField;

    @ApiModelProperty("重定义列")
    private String columns;

    @ApiModelProperty("查询查件")
    private String filter;

    @Column(name = "chmod")
    @ApiModelProperty(hidden = true)
    private int chmod = 0;  // 说明：不开放，不要给get方法

    @ApiModelProperty("支持添加")
    private boolean canAdd = false;
    @ApiModelProperty("支持修改")
    private boolean canUpdate = false;
    @ApiModelProperty("支持删除")
    private boolean canDelete = false;
    @ApiModelProperty("支持批量删除")
    private boolean canBatchDelete = false;
    @ApiModelProperty("支持导入")
    private boolean canImport = false;
    @ApiModelProperty("支持导出")
    private boolean canExport = false;
    @ApiModelProperty("支持自定义列")
    private boolean canCustomColumn = false;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public void setChmod(Integer chmod) {
        this.chmod = chmod;
        this.canAdd = this.has(SYSTEM.MOD_ADD);
        this.canUpdate = this.has(SYSTEM.MOD_UPDATE);
        this.canDelete = this.has(SYSTEM.MOD_DELETE);
        this.canBatchDelete = this.has(SYSTEM.MOD_BATCH_DELETE);
        this.canImport = this.has(SYSTEM.MOD_IMPORT);
        this.canExport = this.has(SYSTEM.MOD_EXPORT);
        this.canCustomColumn = this.has(SYSTEM.MOD_CUSTOM_COLUMN);
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }


    public boolean isCanAdd() {
        return canAdd;
    }

    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
        this.setRole(canAdd, SYSTEM.MOD_ADD);
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
        this.setRole(canUpdate, SYSTEM.MOD_UPDATE);
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        this.setRole(canDelete, SYSTEM.MOD_DELETE);
    }

    public boolean isCanBatchDelete() {
        return canBatchDelete;
    }

    public void setCanBatchDelete(boolean canBatchDelete) {
        this.canBatchDelete = canBatchDelete;
        this.setRole(canBatchDelete, SYSTEM.MOD_BATCH_DELETE);
    }

    public boolean isCanImport() {
        return canImport;
    }

    public void setCanImport(boolean canImport) {
        this.canImport = canImport;
        this.setRole(canImport, SYSTEM.MOD_IMPORT);
    }

    public boolean isCanExport() {
        return canExport;
    }

    public void setCanExport(boolean canExport) {
        this.canExport = canExport;
        this.setRole(canExport, SYSTEM.MOD_EXPORT);
    }

    public boolean isCanCustomColumn() {
        return canCustomColumn;
    }

    public void setCanCustomColumn(boolean canCustomColumn) {
        this.canCustomColumn = canCustomColumn;
        this.setRole(canCustomColumn, SYSTEM.MOD_CUSTOM_COLUMN);
    }

    private boolean has(int action) {
        return (this.chmod & (1 << action)) > 0;
    }

    private void setRole(boolean role, int action) {
        if (role) {
            this.add(action);
        } else {
            this.delete(action);
        }
    }

    private void add(int action) {
        if (this.has(action)) {
            return;
        }
        this.chmod = (this.chmod | (1 << action));
    }

    private void delete(int action) {
        if (this.has(action)) {
            return;
        }
        this.chmod = (this.chmod & (~(1 << action)));
    }
}