package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

public class Resource {
	/**
	 * 资源ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 资源类型
	 */
	@ApiModelProperty(value = "资源名称")
	private String name;

	/**
	 * 标题
	 */
	@ApiModelProperty(value = "标题")
	private String title;

	/**
	 * icon
	 */
	private String icon;

	/**
	 * 所在位置：1=管理页面 2=展示页面 3=大屏页面 4=小组件
	 */
	private Byte place;

	/**
	 * 资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
	 */
	private Byte type;

	/**
	 * 链接地址
	 */
	@ApiModelProperty(value = "链接地址")
	private String path;

	/**
	 * 版本编码
	 */
	@Column(name = "version_code")
	private Integer versionCode;

	/**
	 * 菜单进展 1=未开始 2=待测试 3=可出货
	 */
	private Byte progress;
	

	/**
	 * 三权 1=安全审计员 2=系统管理员 4=安全保密员
	 */
	@Column(name = "three_powers")
	private Byte threePowers;

	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Ignore
	private Integer sort;

	/**
	 * 资源所属的服务ID
	 */
	@Column(name = "service_id")
	@ApiModelProperty(value = "资源所属的服务ID")
	private String serviceId;

	private String uid;

	private String puid;

	/**
	 * 标记，用于全并多个不同项目下了子菜单
	 */
	private String sign;

	/**
	 * 0 可用，1 人为授权禁止 2 证书授权禁止
	 */
	private Byte disabled;


	/**
	 * 0 开发完成，1 开发中
	 */
	@Column(name = "develop_status")
	private Byte developStatus;


	/**
	 * 获取资源ID
	 *
	 * @return id - 资源ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 设置资源ID
	 *
	 * @param id
	 *            资源ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取资源类型
	 *
	 * @return name - 资源类型
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置资源类型
	 *
	 * @param name
	 *            资源类型
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取标题
	 *
	 * @return title - 标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置标题
	 *
	 * @param title
	 *            标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取icon
	 *
	 * @return icon - icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * 设置icon
	 *
	 * @param icon
	 *            icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 获取资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
	 *
	 * @return type - 资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
	 */
	public Byte getType() {
		return type;
	}

	/**
	 * 设置资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
	 *
	 * @param type
	 *            资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）
	 */
	public void setType(Byte type) {
		this.type = type;
	}

	/**
	 * 获取链接地址
	 *
	 * @return path - 链接地址
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 设置链接地址
	 *
	 * @param path
	 *            链接地址
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取排序
	 *
	 * @return sort - 排序
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * 设置排序
	 *
	 * @param sort
	 *            排序
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * 获取资源所属的服务ID
	 *
	 * @return service_id - 资源所属的服务ID
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * 设置资源所属的服务ID
	 *
	 * @param serviceId
	 *            资源所属的服务ID
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return disabled
	 */
	public Byte getDisabled() {
		return disabled;
	}

	/**
	 * @param disabled
	 */
	public void setDisabled(Byte disabled) {
		this.disabled = disabled;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

	public Byte getPlace() {
		return place;
	}

	public void setPlace(Byte place) {
		this.place = place;
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

	public Byte getDevelopStatus() {
		return developStatus;
	}

	public void setDevelopStatus(Byte developStatus) {
		this.developStatus = developStatus;
	}
}