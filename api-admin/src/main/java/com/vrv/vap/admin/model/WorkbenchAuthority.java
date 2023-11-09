package com.vrv.vap.admin.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "workbench_authority")
public class WorkbenchAuthority {



    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;


    /**
     *角色id
     */
    @Column(name = "role_id")
    private String roleId;


    /**
     * 组件code
     */
    @Column
    private  String codes;


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
     * 更新时间
     */
    @Column(name="update_time")
    private Date updateTime;

}
