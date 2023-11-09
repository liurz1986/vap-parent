package com.vrv.vap.alarmdeal.frameworks.contract.audit;

import lombok.Data;

import java.util.List;
@Data
public class Result<T> {
   private String code; //编号
   private String message;
   private List<T> data;
}
