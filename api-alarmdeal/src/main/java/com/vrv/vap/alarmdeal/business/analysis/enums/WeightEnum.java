package com.vrv.vap.alarmdeal.business.analysis.enums;

public enum WeightEnum {

	EMERGENCY("5","紧急"),
	SERIOUS("4","严重"),
	IMPORTANT("3","重要"),
	ORDINARY("2","一般"),
	LOW("1","较低"),
	LOWS("0","低");
	private String weight;
	private String weightName;
	
	WeightEnum(String weight,String weightName){
		this.weight = weight;
		this.weightName  = weightName;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getWeightName() {
		return weightName;
	}

	public void setWeightName(String weightName) {
		this.weightName = weightName;
	}
	
	/**
	 * 获得中文自定义名称
	 * @param weight
	 * @return
	 */
	public static String getWeightName(String weight){
		String weightName = null;
		for (WeightEnum weightEnum : WeightEnum.values()) {
			String weightValue = weightEnum.getWeight();
			if(weightValue.equals(weight)){
				weightName = weightEnum.getWeightName();
				break;
			}
		}
		return weightName;
	}
	
	
}
