package com.vrv.rule.model.filter;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class Exchanges {
     
	 private String id;
     private String type; //过滤器类型
     private List<String> sources; //eventtable表Id集合
     private String target; //目标tableId
     private String options; //扩展属性（目前只包含LogicOperator）
}
