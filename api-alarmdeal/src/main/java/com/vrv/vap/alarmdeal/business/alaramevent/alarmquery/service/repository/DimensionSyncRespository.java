package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmQuery;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.DimensionSync;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: 梁国露
 * @since: 2022/10/12 14:25
 * @description:
 */
@Repository
public interface DimensionSyncRespository extends BaseRepository<DimensionSync,String> {
}
