package com.vrv.rule.ruleInfo.exchangeType.dimension;

import org.apache.commons.lang3.StringUtils;

/**
 * 维表相关工具类
 * @author wudi
 * @date 2022/11/22 14:28
 */
public class DimensionUtil {

        /**
         * 对特殊字符进行处理
         * @param cacheVOStr
         * @return
         */
       public static String subCacheVO(String cacheVOStr){
           if (StringUtils.isNotBlank(cacheVOStr)&&cacheVOStr.contains(">$")) {
               cacheVOStr = cacheVOStr.substring(3);
           }
           return cacheVOStr;
       }

}
