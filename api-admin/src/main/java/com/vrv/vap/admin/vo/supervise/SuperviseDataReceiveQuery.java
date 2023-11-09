package com.vrv.vap.admin.vo.supervise;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@ApiModel(value="级联数据接收")
public class SuperviseDataReceiveQuery extends Query {
	
    @ApiModelProperty("数据类型")
    private Integer dataType;

    // 1 在线接收，2 离线导入
    @ApiModelProperty("接收方式: 1 在线接收 2 离线导入")
    private Integer receiveType;
    
	@ApiModelProperty("接收开始时间")
	@Column(name="receiveTime")
	@QueryMoreThan
    @JsonProperty(value = "beginTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date beginTime;

    @ApiModelProperty("接收结束时间")
	@Column(name="receiveTime")
	@QueryLessThan
    @JsonProperty(value = "endTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
