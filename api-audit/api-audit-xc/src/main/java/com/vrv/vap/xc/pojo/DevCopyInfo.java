package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * <p>
 * 设备拷贝数据量表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-23
 */
@ApiModel(value="DevCopyInfo对象", description="设备拷贝数据量表")
@TableName(value = "rpt_dev_copy_info")
public class DevCopyInfo  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户key")
    private String userKey;

    @ApiModelProperty(value = "目标主机ip")
    private String dstIp;

    @ApiModelProperty(value = "统计时间")
    private String dataTime;

    @ApiModelProperty(value = "拷贝数量")
    private Integer count;

    @ApiModelProperty(value = "入库时间")
    private Date time;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
