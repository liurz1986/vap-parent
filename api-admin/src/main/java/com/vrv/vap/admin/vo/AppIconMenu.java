package com.vrv.vap.admin.vo;

import java.util.ArrayList;
import java.util.List;


public class AppIconMenu {

	private int  appid;
	private String  title;
	private String  icon;
	private Byte  type;
	private String  path;
    private int  sort;
	private Byte  folder;
	private List<AppIconMenu> children;
	 
	public int getAppid() {
		return appid;
	}
	public void setAppid(int appid) {
		this.appid = appid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}	
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public List<AppIconMenu> getChildren() {
		return children;
	}
	public void setChildren(List<AppIconMenu> children) {
		this.children = children;
	}

	public Byte getFolder() {
		return folder;
	}

	public void setFolder(Byte folder) {
		this.folder = folder;
	}

	public void appendChild(AppIconMenu menu){
		if(this.children==null){
			this.children = new ArrayList<>();
		}
		this.children.add(menu);
	}
}
