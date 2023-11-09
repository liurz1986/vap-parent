package com.vrv.vap.alarmdeal.business.buinesssystem.service;

import com.vrv.vap.alarmdeal.business.buinesssystem.model.BuinessSystem;
import com.vrv.vap.alarmdeal.business.buinesssystem.vo.*;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface BuinessSystemService extends BaseService<BuinessSystem, String>{

   public BuinessSystemTreeVO getAllTree();

    public PageRes<BuinessSystemVO> getInfoPager(BuinessSystemSearchVO buinessSystemSearchVO, Pageable pageable);

    public Result<BuinessSystemVO> getBusiSystem(String busId);

    public List<BusiAssetVO> getAssetByBusId(String busId);

    public Result<Boolean> saveBusi(BuinessSystemSaveVO buinessSystemSaveVO);

    public Result<Boolean> saveEdit(BuinessSystemSaveVO buinessSystemSaveVO);

    public  Result<Boolean> deleteBusi(BuinessSystemVO buinessSystemVO);

   public Result<List<ParentBusiVO>> getParentBusi();


}
