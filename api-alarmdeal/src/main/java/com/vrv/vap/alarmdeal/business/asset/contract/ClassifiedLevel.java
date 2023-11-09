package com.vrv.vap.alarmdeal.business.asset.contract;

import lombok.Data;

/**
 * 涉密等级
 *
 */
@Data
public class ClassifiedLevel {
      private int id;

      private String code;

      private String codeValue;

      private String type;

      private String parentType;

      private String leaf;

      private String description;

      private String createId;

      private String createTime;

      private String updateId;

      private String updateTime;

      private String sort;
}