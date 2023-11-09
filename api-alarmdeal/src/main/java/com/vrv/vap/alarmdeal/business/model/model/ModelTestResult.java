package com.vrv.vap.alarmdeal.business.model.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 模型测试结果记录表
 */
@Data
@Table(name="model_test_result")
@Entity
@ApiModel(value = "模型测试结果记录表")
public class ModelTestResult {
     @Id
     private String guid;
     @Column(name="model_manage_id")
     private String modelManageId; // 模型配置guid
     @Column(name="status")
     private int status; // 测试结果 0：通过、-1：测试未通过
     @Column(name="create_user")
     private String createUser;//测试人
     @Column(name="create_time")
     private Date createTime; //测试时间


}
