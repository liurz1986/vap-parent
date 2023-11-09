package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.MachineRoom;
import com.vrv.vap.alarmdeal.business.asset.repository.MachineRoomRepository;
import com.vrv.vap.alarmdeal.business.asset.service.MachineRoomService;
import com.vrv.vap.alarmdeal.business.asset.vo.MachineRoomVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachineRoomServiceImpl  extends BaseServiceImpl<MachineRoom, String> implements MachineRoomService {

    @Autowired
    private MachineRoomRepository machineRoomRepository;

    @Override
    public MachineRoomRepository getRepository() {
        return machineRoomRepository;
    }

    @Override
    public List<MachineRoom> getMachineRoomGrid(String guid){
        List<MachineRoom> machinerooms = new ArrayList<>();
        if(StringUtils.isNotEmpty(guid)){
            MachineRoom machineRoom = getOne(guid);
            machinerooms.add(machineRoom);
        }else{
            Sort sort = Sort.by(Sort.Direction.ASC, "sort"); //根据sort进行排序
            machinerooms = findAll(sort);
        }
        return machinerooms;
    }

    @Override
    public PageRes<MachineRoom> getMachineRoomInfoPager(MachineRoomVO machineRoomVO, Pageable pageable) {
        String code = machineRoomVO.getCode();
        List<QueryCondition> cons = new ArrayList<>();
        if(StringUtils.isNotEmpty(code)){
            QueryCondition condition = QueryCondition.like("code", code);
            cons.add(condition);
        }
        Page<MachineRoom> page = findAll(cons, pageable);
        PageRes<MachineRoom> res = PageRes.toRes(page);
        return res;
    }


}
