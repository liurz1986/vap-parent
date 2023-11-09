package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.dao;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/9/28
 */

public interface EventCategoryDao {

    public List<Map<String, Object>> getGetSecondLevelEvent();

}
