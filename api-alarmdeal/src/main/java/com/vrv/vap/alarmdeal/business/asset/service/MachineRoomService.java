package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.model.MachineRoom;
import com.vrv.vap.alarmdeal.business.asset.vo.MachineRoomVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MachineRoomService extends BaseService<MachineRoom, String> {


    /**
     * 获得机房列表（无分页）
     * @param guid
     * @return
     */
    public List<MachineRoom> getMachineRoomGrid(String guid);

    /**
     * 获取机房列表
     * @param machineRoomVO
     * @param pageable
     * @return
     */
    public PageRes<MachineRoom> getMachineRoomInfoPager(MachineRoomVO machineRoomVO, Pageable pageable);


}
