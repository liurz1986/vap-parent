package com.vrv.vap.alarmdeal.business.model.model;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 模型管理表
 */
@Data
@Table(name="model_manage")
@Entity
@ApiModel(value = "模型管理表")
public class ModelManage {
    @Id
    private String guid; //模型配置id
    @Column(name="model_id")
    private String modelId;// 模型id
    @Column(name="model_name")
    private String modelName;// 模型名称
    @Column(name="version")
    private String version;//模型版本
    @Column(name="label")
    private String label ;// 标签
    @Column(name="version_desc")
    private String versionDesc; //版本说明
    @Column(name="model_desc")
    private String modelDesc;// 模型说明
    @Column(name="model_file_name")
    private String modelFileName;//导入模型名称文件名称
    @Column(name="model_file_path")
    private String modelFilePath;//资源导入文件全路径
    @Column(name="model_version_create_time")
    private Date modelVersionCreateTime; // 版本创建时间
    @Column(name="model_test_url")
    private String modelTestUrl; // 模型测试接口URL
    @Column(name="model_run_url")
    private String modelRunUrl; // 模型运行接口URL
    @Column(name="data_customer_model")
    private String dataCustomerModel; //数据消费模式: 一次性消费、周期性消费
    @Column(name="data_customer_period")
    private String dataCustomerPeriod; // 数据消费周期表达式
    @Column(name="model_input_params")
    private String modelInputParams; // 模型入参集:json字符串
    @Column(name="model_start_parm")
    private String modelStartParam; // 模型启动参数
    @Column(name="model_log_path")
    private String modelLogPath; // 模型日志记录路径
    @Column(name="model_log_level")
    private String modelLogLevel; // 模型日志级别   debug，info，warn，error，Fatal
    @Column(name="used")
    private int used ;// 是否可用 ：0：可用，-1：不可用。新增模型默认为0，模型编辑后，会新生成一条记录，启动成功后，该条记录改为0，历史的变为-1
    @Column(name="status")
    private int status ;// 状态 ：1（待测试）、2（已测试）、3(已发布)、4（启动中)、5（停用中)、6(已下架)
    @Column(name="is_delete")
    private int isDelete ;// 是否删除  默认是0，-1表示删除状态
    @Column(name="create_user")
    private String createUser; //创建人
    @Column(name="create_time")
    private Date createTime; //创建时间
    @Column(name="update_user")
    private String updateUser; //更新人
    @Column(name="update_time")
    private Date updateTime; //更新时间
    @Column(name="remark")
    private String remark; //备注
}
