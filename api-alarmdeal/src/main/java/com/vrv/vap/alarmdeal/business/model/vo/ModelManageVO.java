package com.vrv.vap.alarmdeal.business.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 模型配置信息
 */
@Data
public class ModelManageVO  {
       private String guid; //模型配置id

       private String modelId;// 模型id

       private String modelName;// 模型名称

       private String version;//模型版本

       private String label ;// 标签

       private String versionDesc; //版本说明

       private String modelDesc;// 模型说明

       private String modelFileName;//导入模型名称文件名称

       private String modelFilePath;//资源导入文件全路径

       @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
       private Date modelVersionCreateTime; // 版本创建时间

       private String modelTestUrl; // 模型测试接口URL

       private String modelRunUrl; // 模型运行接口URL

       private String dataCustomerModel; //数据消费模式: 一次性消费、周期消费

       private String dataCustomerPeriod; // 数据消费周期

       private String modelInputParams; // 模型入参集:json字符串

       private String modelStartParam; // 模型启动参数

       private String modelLogPath; // 模型日志记录路径

       private String modelLogLevel; // 模型日志级别   debug，info，warn，error，Fatal

       private int used ;// 是否可用 ：0：可用，-1：不可用。新增模型默认为0，模型编辑后，会新生成一条记录，启动成功后，该条记录改为0，历史的变为-1

       private int status ;// 状态 ：1（待测试）、2（已测试）、3(已发布)、4（启动中)、5（停用中)、6(已下架)

       private int isDelete ;// 是否删除  默认是0，-1表示删除状态

       private String createUser; //创建人
       @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
       private Date createTime; //创建时间

       private String updateUser; //更新人
       @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
       private Date updateTime; //更新时间

       // 模型参数制定数据
       private List<ModelParamConfig> paramList;

       private String modelFileGuid; // 导入模型文件的唯一标识,主要是为找到对应模型jar包 对应modelFilePath的值

       private String remark;

}
