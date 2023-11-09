package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.jpa.web.NameValue;
import lombok.Data;

import java.util.List;

@Data
public class PageCustomColumnVO extends PageColumnVO {

  List<NameValue>	successData;
  List<NameValue>	  failData;
  
  public String getHash()
  {
	  return name+"-|-"+title+"-|-"+type;
  }
}
