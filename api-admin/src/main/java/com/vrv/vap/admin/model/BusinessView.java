package com.vrv.vap.admin.model;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name = "business_view")
public class BusinessView {
	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 用户GUID
	 */
	@Column(name = "user_id")
	private Integer userId;

	/**
	 * 展示图片地址
	 */
	@Column(name = "img_url")
	private String imgUrl;

	/**
	 * tip名称
	 */
	private String title;

	/**
	 * 链接地址
	 */
	private String url;

	/**
	 * 简介
	 */
	private String intro;

	/**
	 * 大屏展示表guid
	 */
	@Column(name = "bs_guid")
	private String bsGuid;
	
    /**
     * 添加时间
     */
    @Column(name = "add_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date addTime;
    
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getBsGuid() {
		return bsGuid;
	}

	public void setBsGuid(String bsGuid) {
		this.bsGuid = bsGuid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

}