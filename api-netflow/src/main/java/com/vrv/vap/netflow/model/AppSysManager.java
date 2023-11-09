package com.vrv.vap.netflow.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "app_sys_manager")
public class AppSysManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_no")
    private String appNo;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_guid")
    private String departmentGuid;

    @Column(name = "domain_name")
    private String domainName;

    @Column(name = "secret_level")
    private Integer secretLevel;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "secret_company")
    private String secretCompany;

    @Column(name = "service_id")
    private String serviceId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return app_no
     */
    public String getAppNo() {
        return appNo;
    }

    /**
     * @param appNo
     */
    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    /**
     * @return app_name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * @param appName
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return department_name
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * @param departmentName
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * @return department_guid
     */
    public String getDepartmentGuid() {
        return departmentGuid;
    }

    /**
     * @param departmentGuid
     */
    public void setDepartmentGuid(String departmentGuid) {
        this.departmentGuid = departmentGuid;
    }

    /**
     * @return domain_name
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * @param domainName
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * @return secret_level
     */
    public Integer getSecretLevel() {
        return secretLevel;
    }

    /**
     * @param secretLevel
     */
    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return secret_company
     */
    public String getSecretCompany() {
        return secretCompany;
    }

    /**
     * @param secretCompany
     */
    public void setSecretCompany(String secretCompany) {
        this.secretCompany = secretCompany;
    }

    /**
     * @return service_id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "AppSysManager{" +
                "id=" + id +
                ", appNo='" + appNo + '\'' +
                ", appName='" + appName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentGuid='" + departmentGuid + '\'' +
                ", domainName='" + domainName + '\'' +
                ", secretLevel=" + secretLevel +
                ", createTime=" + createTime +
                ", secretCompany='" + secretCompany + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}