package com.vrv.vap.line.pojo;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2023-06-06
 */
@ApiModel(value="PasswordKey对象", description="")
public class PasswordKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "关键字")
    @TableField("`key`")
    private String key;

    @ApiModelProperty(value = "创建时间")
    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "PasswordKey{" +
            "id=" + id +
            ", key=" + key +
            ", insertTime=" + insertTime +
        "}";
    }
}
