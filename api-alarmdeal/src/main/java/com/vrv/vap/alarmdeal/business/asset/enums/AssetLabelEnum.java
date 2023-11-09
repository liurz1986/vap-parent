package com.vrv.vap.alarmdeal.business.asset.enums;

import org.apache.commons.lang3.StringUtils;

public enum AssetLabelEnum {
	FAILED_STATUS(1,"失陷状态","failed_status","fail_tag")//可疑（1）、高危（2）、失陷（3）。（解除状态是一种隐藏状态，表示安全（0）
	,LINK_SYS(2,"绑定应用系统","link_sys","link_sys") //0未绑定  1绑定
	,LINK_VUL(3,"存在漏洞","link_vul","link_vul") //0不存在 1存在
	,LINK_ALARM(4,"存在告警","link_alarm","link_alarm") //0不存在 1存在
	//,Vul_Level(3,"漏洞等级","vul_level","vul_level") //0无漏洞  1 存在1级  2 存在3级 ....
	;
	
	
	
	

	//列的顺序
	private int index;
	//中文展示名称
	private String labelTile;
	//对应的数据库字段列
	private String labelName;
	//topic中传过来了的tagName
	private String tagName;
	
	
 

	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}

 

	public String getTagName() {
		return tagName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public String getLabelTile() {
		return labelTile;
	}


	public void setLabelTile(String labelTile) {
		this.labelTile = labelTile;
	}


	public String getLabelName() {
		return labelName;
	}


	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}


	AssetLabelEnum(int index, String labelTile, String labelName, String tagName){
		this.index=index;
		this.labelTile = labelTile;
		this.labelName = labelName;
		this.tagName=tagName;
	}
	
 
 
	 public static AssetLabelEnum getTypeByTagName(String name){
	        if (StringUtils.isEmpty(name)){
	            return null;
	        } 
	        for (AssetLabelEnum enums : AssetLabelEnum.values()) {
	            if (enums.getTagName().equals(name)) {
	                return enums;
	            }
	        }
	        return null;
	    }
	
	public static String getLikeStr(AssetLabelEnum _enum,String value) {
		String str="";
		AssetLabelEnum[] values = AssetLabelEnum.values();
		for(int i=1;i<=values.length;i++) {
				if(_enum.getIndex()==i) {
					str=str+value+"|";
				}else {
					str=str+"%|";
				}
		}
		return str;
	}
	
/*	public static void main(String[] args) {
		String colsStr="";
		if(StringUtils.isEmpty(colsStr))  {
			List<String>  list=new LinkedList<>();
			AssetLabelEnum[] values = AssetLabelEnum.values();
			for(int i=1;i<=values.length;i++) {
				for(AssetLabelEnum item : values) {
					list.add("`asset_labels`.`"+item.getLabelName()+"`,'|'");
				}
			}
			colsStr= StringUtils.join(list.toArray(),",");
		}
		System.out.println(colsStr);
	}*/
}
