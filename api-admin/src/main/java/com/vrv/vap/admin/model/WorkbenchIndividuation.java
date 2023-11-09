package com.vrv.vap.admin.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "workbench_individuation")
public class WorkbenchIndividuation {


    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;


    /**
     *用户id
     */
    @Column(name = "user_id")
    private Integer userId;


    /**
     *工作台配置参数
     */
    @Column(name="workbench_config")
    private String workbenchConfig;

    /**
     * 创建时间
     */
    @Column(name="create_time")
    private Date createTime;

    /**
     * 组件权限
     */
    private String codes;



    /**
     * 更新时间
     */
    @Column(name="update_time")
    private Date updateTime;




}
