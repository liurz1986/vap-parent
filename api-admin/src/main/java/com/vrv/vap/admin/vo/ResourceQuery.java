package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;

import io.swagger.annotations.ApiModelProperty;
/**
 * ResourceQuery
 * */
public class ResourceQuery extends Query{

    /**
     * 资源名称（内部）
     */
	@ApiModelProperty("资源名称（内部）")
	@QueryLike
    private String name;

    /**
     * 标题
     */
	@ApiModelProperty("标题")
	@QueryLike
    private String title;

    /**
     * 资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
     */
	@ApiModelProperty("资源类型")
    private String type;

    /**
     * 链接地址
     */
	@ApiModelProperty("链接地址")
    @QueryLike
    private String path;

    /**
     * 资源所属的服务ID
     */
	@ApiModelProperty("资源所属的服务ID")
    private String serviceId;


    /**
     * 版本编码
     */
	@ApiModelProperty("版本编码")
    private Integer versionCode;

    /**
     * 菜单进展 1=未开始 2=待测试 3=可出货
     */
	@ApiModelProperty("菜单进展")
    private Byte progress;


    /**
     * 三权 1=安全审计员 2=系统管理员 4=安全保密员
     */
	@ApiModelProperty("三权")
    private Byte threePowers;


    private String uid;

    private String puid;


	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Byte getProgress() {
        return progress;
    }

    public void setProgress(Byte progress) {
        this.progress = progress;
    }

    public Byte getThreePowers() {
        return threePowers;
    }

    public void setThreePowers(Byte threePowers) {
        this.threePowers = threePowers;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }
}
