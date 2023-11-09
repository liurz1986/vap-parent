package com.vrv.vap.admin.vo;
import com.vrv.vap.admin.model.User;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.List;

public class BaseSecurityDomainTreeVO {

   private List<User>  userList;

    /**
     * 关联使用guid
     */
    private String code;

    /**
     * 安全域id
     */
    private Integer domainId;

    /**
     * 上级编号
     */
    private String parentCode;
    /**
     * 安全域名称
     */
    private String domainName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 安全域是否已授权  0 未授权，1授权
     */
    private Integer isAuthorized;


    private List<BaseSecurityDomainTreeVO> children;

    private Byte orghierarchy;

    private String subCode;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级  0绝密，1机密，2秘密，3内部")

    private Integer secretLevel;

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Integer isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public List<BaseSecurityDomainTreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<BaseSecurityDomainTreeVO> children) {
        this.children = children;
    }


    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Byte getOrghierarchy() {
        return orghierarchy;
    }

    public void setOrghierarchy(Byte orghierarchy) {
        this.orghierarchy = orghierarchy;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    //    @Override
//    public  boolean equals(Object obj){
//        if(this == obj){return  true;}
//        if(obj == null || this.getClass() != obj.getClass()){
//            return  false;
//        }
//        BaseSecurityDomainTreeVO bd = (BaseSecurityDomainTreeVO)obj;
//        return  this.code.equals(bd.code);
//    }
//
//    @Override
//    public int hashCode(){
//        String in = code;
//        return  in.hashCode();
//    }
}
