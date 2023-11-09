package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.TerminalAssteInstallTime;
import com.vrv.vap.alarmdeal.business.asset.repository.TerminalAssteInstallTimeRepository;
import com.vrv.vap.alarmdeal.business.asset.service.TerminalAssteInstallTimeService;
import com.vrv.vap.alarmdeal.business.asset.vo.TerminalAssteInstallTimeJobVO;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TerminalAssteInstallTimeServiceImpl extends BaseServiceImpl<TerminalAssteInstallTime, String> implements TerminalAssteInstallTimeService {

    private static Logger logger = LoggerFactory.getLogger(TerminalAssteInstallTimeServiceImpl.class);

    @Autowired
    private TerminalAssteInstallTimeRepository terminalAssteInstallTimeRepository;
    @Override
    public BaseRepository<TerminalAssteInstallTime, String> getRepository() {
        return terminalAssteInstallTimeRepository;
    }

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private TerminalAssteInstallTimeService terminalAssteInstallTimeService;

    @Override
    public void excTerminalAssteInstallTime(TerminalAssteInstallTimeJobVO data){
        String type = data.getType();
        List<String> guids = data.getGuids();
        switch (type){
            case "1":
                saveAssetTerminalInstallTime(data.getAsset());  // 新增
                break;
            case "2":
                updateTerminalAssteInstallTime(data.getOldOsSetupTime(),data.getAsset()); // 修改
                break;
            case "3":
                deleteTerminalAssteInstallTime(guids); //删除
                break;
            case "4":
                allUpdateTerminalAssteInstallTime(); //资产导入、资产待审库批量入库、自动入库
                break;
            default:
                break;
        }
    };

    /**
     * 全量更新处理:定时更新，王竹要求的
     *  1.定时更新last_install_time、current_install_time的值
     */
    public void excUpdateTerminalAssteInstallTime() {
        try{
            logger.info("终端设备更新上一次系统安装时间开始");
            // 更新last_install_time的值，条件是asset_terminal_install_time表中current_install_time的值与assset表os_setup_time比较，没有变化将last_install_time的值更新为当前的系统安装时间，有变化不处理
            assetDao.updateLastInstallTime();
            // 更新current_install_time的值，直接将asset_terminal_install_time表中current_install_time的值更新为asset中系统安装时间
            assetDao.updateCurrentInstallTime();
            logger.info("终端设备更新上一次系统安装时间结束");
        }catch(Exception e){
            logger.error("终端设备更新上一次系统安装时间异常",e);
        }
    }

    /**
     * 资产导入、资产待审库批量入库
     * 获取新增数据，进行新增处理
     * 已经存在数据进行更新处理
     */
    public void allUpdateTerminalAssteInstallTime(){
        logger.info("allUpdateTerminalAssteInstallTime开始");
        // 对修改的数据：更新last_install_time的值
        assetDao.updateLastInstallTimeByAsset();
        // 对于新增的数据
        List<TerminalAssteInstallTime> newSaves = assetDao.getTerminalAssteInstallTime();
        if(CollectionUtils.isNotEmpty(newSaves)){
            terminalAssteInstallTimeService.save(newSaves);
        }

    }
    /**
     * 更新处理
     * @param oldOsSetupTime
     * @param asset
     * @return
     */
    private void updateTerminalAssteInstallTime(Date oldOsSetupTime, Asset asset) {
        String typeUniqueCode = asset.getTypeUnicode();
        if (!isTerminalAsset(typeUniqueCode)) { // 资产类型是不是终端
            return ;
        }
        List<QueryCondition> con1 = new ArrayList<>();
        con1.add(QueryCondition.eq("assetGuid", asset.getGuid()));
        List<TerminalAssteInstallTime> terms = terminalAssteInstallTimeService.findAll(con1);
        // 存在就更新，不存在就新增
        if (null != terms && terms.size() > 0) {
            TerminalAssteInstallTime ter = terms.get(0);
            ter.setLastInstallTime(oldOsSetupTime);
            terminalAssteInstallTimeService.save(ter);
            return ;
        }
        TerminalAssteInstallTime ter = new TerminalAssteInstallTime();
        ter.setAssetGuid(asset.getGuid());
        ter.setLastInstallTime(oldOsSetupTime);
        ter.setGuid(UUIDUtils.get32UUID());
        terminalAssteInstallTimeService.save(ter);
    }

    /**
     * 新增处理
     * @param asset
     * @return
     */
    private void saveAssetTerminalInstallTime(Asset asset) {
        String typeUniqueCode = asset.getTypeUnicode();
        if (!isTerminalAsset(typeUniqueCode)) { // 资产类型是不是终端
            return ;
        }
        TerminalAssteInstallTime ter = new TerminalAssteInstallTime();
        ter.setAssetGuid(asset.getGuid());
        ter.setLastInstallTime(asset.getOsSetuptime());
        ter.setGuid(UUIDUtils.get32UUID());
        terminalAssteInstallTimeService.save(ter);
    }

    /**
     * 删除处理
     * @param guids
     */
    private void deleteTerminalAssteInstallTime(List<String> guids) {
        List<QueryCondition> con1 = new ArrayList<>();
        con1.add(QueryCondition.in("assetGuid", guids));
        List<TerminalAssteInstallTime> terms = terminalAssteInstallTimeService.findAll(con1);
        if (null != terms && terms.size() > 0) {
            terminalAssteInstallTimeService.deleteInBatch(terms);
        }
    }


    // 当前设备是不是终端类型
    public boolean isTerminalAsset(String typeUniqueCode){
        int count =  assetDao.terminalAssetByTypeUniqueCode(typeUniqueCode);
        if(count > 0){
            return true;
        }
        return false;
    }
}
