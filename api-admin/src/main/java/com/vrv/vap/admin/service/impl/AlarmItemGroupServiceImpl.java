package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.AlarmItemGroupMapper;
import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.admin.service.AlarmItemGroupService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lilang
 * @date 2022/10/14
 * @description
 */
@Service
@Transactional
public class AlarmItemGroupServiceImpl extends BaseServiceImpl<AlarmItemGroup> implements AlarmItemGroupService {

    @Resource
    AlarmItemGroupMapper alarmItemGroupMapper;

    @Override
    public void initGroupData() {
        List<AlarmItemGroup> groupList = alarmItemGroupMapper.getGroupData();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (AlarmItemGroup alarmItemGroup : groupList) {
                AlarmItemGroup itemGroup = new AlarmItemGroup();
                itemGroup.setAlarmType(alarmItemGroup.getAlarmType());
                itemGroup.setAlarmLevel(alarmItemGroup.getAlarmLevel());
                itemGroup.setAlarmSource(alarmItemGroup.getAlarmSource());
                itemGroup.setAlarmDesc(alarmItemGroup.getAlarmDesc());
                List<AlarmItemGroup> groups = alarmItemGroupMapper.select(itemGroup);
                if (CollectionUtils.isEmpty(groups)) {
                    alarmItemGroupMapper.insert(itemGroup);
                }
            }
        }
    }
}
