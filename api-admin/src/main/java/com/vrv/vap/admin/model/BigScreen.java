package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "big_screen")
public class BigScreen {
	/**
	 * guid
	 */
	@Id
	private String guid;

	/**
	 * 大屏展示标题
	 */
	private String title;

	/**
	 * 简介
	 */
	private String intro;

	/**
	 * 大屏展示链接地址
	 */
	private String url;

	/**
	 * 展示图片地址
	 */
	private String img;

	/**
	 * 分辨率
	 */
	private String resolution;

	/**
	 * 是否显示0：显示，1：不显示
	 */
	private Integer flag;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

}