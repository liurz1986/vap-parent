package com.vrv.vap.alarmdeal.business.asset.online.service.impl;

import com.vrv.vap.alarmdeal.business.asset.online.Repository.AssetChangeRepository;
import com.vrv.vap.alarmdeal.business.asset.online.constant.AssetChangeConstant;
import com.vrv.vap.alarmdeal.business.asset.online.model.AssetChange;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetChangeService;
import com.vrv.vap.alarmdeal.business.asset.online.util.AssetOnLineExecutorServiceUtil;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetChangeVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetChangeVO;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 资产变更
 *
 * 2022-08
 */
@Service
@Transactional
public class AssetChangeServiceImpl extends BaseServiceImpl<AssetChange, String> implements AssetChangeService {
    private static Logger logger = LoggerFactory.getLogger(AssetChangeServiceImpl.class);
    @Autowired
    private AssetChangeRepository assetChangeRepository;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private int batchNum = 500;  // 批量保存一次保存的最多条数
    @Override
    public BaseRepository<AssetChange, String> getRepository() {
        return assetChangeRepository;
    }

    /**
     * 分页查询
     * 按时间，处理人，处理意见进行一个或多个条件的检索
     * @param serachAssetChangeVO
     * @return
     */
    @Override
    public PageRes<AssetChange> query(SerachAssetChangeVO serachAssetChangeVO) {
        PageReq pageReq=new PageReq();
        pageReq.setCount(serachAssetChangeVO.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("createTime");
        pageReq.setStart(serachAssetChangeVO.getStart_());
        // 获取查询条件
        List<QueryCondition> conditions = searchAssetChangeCondition(serachAssetChangeVO);
        Page<AssetChange> pages =this.findAll(conditions,pageReq.getPageable());
        List<AssetChange> lists = pages.getContent();
        long total = pages.getTotalElements();
        PageRes<AssetChange> res = new PageRes();
        res.setList(lists);
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(total);
        return res;
    }

    // 查询条件
    private List<QueryCondition> searchAssetChangeCondition(SerachAssetChangeVO serachAssetChangeVO) {
        List<QueryCondition> conditions = new ArrayList<>();
        //时间
        if(null != serachAssetChangeVO.getStartTime() && null != serachAssetChangeVO.getEndTime()){
            conditions.add(QueryCondition.between("handleTime", serachAssetChangeVO.getStartTime(),serachAssetChangeVO.getEndTime()));
        }
        // 处理人
        if(StringUtils.isNotEmpty(serachAssetChangeVO.getHandleUserName())){
            conditions.add(QueryCondition.like("handleUserName", serachAssetChangeVO.getHandleUserName()));
        }
        // 处理意见
        if(StringUtils.isNotEmpty(serachAssetChangeVO.getOpinion())){
            conditions.add(QueryCondition.like("opinion", serachAssetChangeVO.getOpinion()));
        }
        return  conditions;
    }

    /**
     * 处理操作
     * 1. 处理意见
     * 2. 处理人
     * 3. 处理时间
     *
     * @param assetChangeVO
     * @return
     */
    @Override
    public Result<AssetChange> handle(AssetChangeVO assetChangeVO) {
        AssetChange assetChange=  this.getOne(assetChangeVO.getGuid());
        if(null == assetChange){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前数据不存在");
        }
        if(AssetChangeConstant.FINISH.equals(assetChange.getHandleStatus())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"该数据已经处理");
        }
        assetChange.setOpinion(assetChangeVO.getOpinion());
        assetChange.setHandleTime(new Date());
        assetChange.setHandleUserId(SessionUtil.getCurrentUser().getId()+"");
        assetChange.setHandleUserName(SessionUtil.getCurrentUser().getName());
        assetChange.setHandleStatus(AssetChangeConstant.FINISH);   // 表示已经处理
        this.save(assetChange);
        return ResultUtil.success(assetChange);
    }

    /**
     * 批量保存
     * @param updateChanges
     */
    @Override
    public void batchSave(List<AssetChange> updateChanges) {
        // 500保存一次
        if (updateChanges.size() <= batchNum) {
            this.save(updateChanges);
        } else {
            List<AssetChange> saveDatas = updateChanges.subList(0, batchNum);
            this.save(saveDatas);
            updateChanges.removeAll(saveDatas);
            batchSave(updateChanges);
        }
    }


    @Override
    public Future<List<AssetChange>> getAllAssetChangesFuture() {
        Future<List<AssetChange>> future = AssetOnLineExecutorServiceUtil.getAssetOnLinePool().submit(new Callable() {
            @Override
            public List<AssetChange> call() throws Exception {
                return assetChangeRepository.findAll();
            }
        });
        return future;
    }

    @Override
    public Long getWarmCount() {
        List<QueryCondition> conditionc = new ArrayList<>();
        conditionc.add(QueryCondition.notEq("handleStatus","0"));
        return this.count(conditionc);
    }
}
