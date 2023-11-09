package com.vrv.vap.admin.vo;
/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年8月30日 下午2:15:28
 * 类说明：Log Statistics Condition VO
 */
public class LogStatisticsConditionVO {

	private String category="";
	private String sourceIp="";
	private String area="";
	private String channel="";
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSourceIp() {
		return sourceIp;
	}
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}
