package com.vrv.rule.source.datasourceconnector.es.vo;

import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SortVO {

      private List<QueryCondition_ES> conditions;
      private String key;
      private String order;
      private Integer size;
      private Long time;

}
