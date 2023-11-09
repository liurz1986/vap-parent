package com.vrv.vap.alarmdeal.business.buinesssystem.service.impl;

import com.vrv.vap.alarmdeal.business.buinesssystem.model.BuinessSystem;
import com.vrv.vap.alarmdeal.business.buinesssystem.model.BuinessSystemAsset;
import com.vrv.vap.alarmdeal.business.buinesssystem.repository.BuinessSystemRepository;
import com.vrv.vap.alarmdeal.business.buinesssystem.service.BuinessSystemService;
import com.vrv.vap.alarmdeal.business.buinesssystem.service.SysdomainAssetService;
import com.vrv.vap.alarmdeal.business.buinesssystem.vo.*;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *业务系统
 *
 *
 * 2022-11-16
 */
@Service
public class BuinessSystemServiceImpl extends BaseServiceImpl<BuinessSystem, String> implements BuinessSystemService {
    @Autowired
    private BuinessSystemRepository buinessSystemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private SysdomainAssetService sysdomainAssetService;

    private String rootCode="0";

    private String rootName="业务系统";

    @Override
    public BaseRepository<BuinessSystem, String> getRepository() {
        return this.buinessSystemRepository;
    }

    /**
     * 业务系统树结构
     * @return
     */
    @Override
    public BuinessSystemTreeVO getAllTree() {
        BuinessSystemTreeVO root =new BuinessSystemTreeVO();
        root.setName(rootName);
        root.setCode(rootCode);
        treeNode(root);
        return root;
    }

   private void treeNode(BuinessSystemTreeVO rootNode){
       List<BusinessNodeVO> datas = getAllSysnames();
       if(CollectionUtils.isEmpty(datas)){
           return;
       }
       addChidrenSysname(datas,rootNode);

   }

    private void addChidrenSysname(List<BusinessNodeVO> datas,BuinessSystemTreeVO parentNode) {
        if(CollectionUtils.isEmpty(datas)){
            return;
        }
        List<BusinessNodeVO> childrens = getChildrens(parentNode,datas);
        if(CollectionUtils.isEmpty(childrens)){
            return;
        }
        datas.removeAll(childrens);
        for(BuinessSystemTreeVO vo : parentNode.getChildren()) {
            addChidrenSysname(datas, vo);
        }
    }

    private List<BusinessNodeVO> getChildrens(BuinessSystemTreeVO parentNode,List<BusinessNodeVO> datas) {
        List<BuinessSystemTreeVO> childrens = new ArrayList<>();
        List<BusinessNodeVO> childrenNodes = new ArrayList<>();
        BuinessSystemTreeVO node = null;
        for(BusinessNodeVO tree : datas){
            if(parentNode.getCode().equals(tree.getParentId())){
                node = new BuinessSystemTreeVO();
                node.setCode(tree.getCode());
                node.setName(tree.getName());
                childrens.add(node);
                childrenNodes.add(tree);
            }
        }
        if(childrens.size() > 0){
            parentNode.setChildren(childrens);
        }
        return childrenNodes;
    }

    private List<BusinessNodeVO> getAllSysnames() {
        String sql = "select guid as code,sys_name as name ,parent_id as parentId from busisystem_combination order by create_time desc ";
        List<BusinessNodeVO> childrens = jdbcTemplate.query(sql,new BeanPropertyRowMapper<BusinessNodeVO>(BusinessNodeVO.class));
        if(CollectionUtils.isEmpty(childrens)){
            return new ArrayList<>();
        }
        return childrens;
    }

    /**
     * 业务系统查询
     * @param buinessSystemSearchVO
     * @param pageable
     * @return
     */
    @Override
    public PageRes<BuinessSystemVO> getInfoPager(BuinessSystemSearchVO buinessSystemSearchVO, Pageable pageable) {
        List<QueryCondition> conditions = getSearchConditions(buinessSystemSearchVO);
        Page<BuinessSystem> pager = this.findAll(conditions,pageable);
        List<BuinessSystem> content = pager.getContent();
        List<BuinessSystemVO> list = new ArrayList<>();
        getBuinessSystemVOList(content, list);
        PageRes<BuinessSystemVO> pageRes = new PageRes<>();
        long totalElements = pager.getTotalElements();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setList(list);
        pageRes.setTotal(totalElements);
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        return pageRes;
    }




    private void getBuinessSystemVOList(List<BuinessSystem> content, List<BuinessSystemVO> list) {
        BuinessSystemVO vo = null;
        for(BuinessSystem buinessSystem : content){
            vo = new BuinessSystemVO();
            BuinessSystemVO sysVO = mapper.map(buinessSystem, BuinessSystemVO.class);
            list.add(sysVO);
        }
    }


    // 业务系统名称、责任人进行模糊匹配查询
    private List<QueryCondition> getSearchConditions(BuinessSystemSearchVO buinessSystemSearchVO) {
        String code = buinessSystemSearchVO.getCode();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("parentId",code));
        // 业务系统名称
        if(StringUtils.isNotEmpty(buinessSystemSearchVO.getSysName())){
            conditions.add(QueryCondition.like("sysName",buinessSystemSearchVO.getSysName()));
        }
        // 责任人
        if(StringUtils.isNotEmpty(buinessSystemSearchVO.getMaintainer())){
            conditions.add(QueryCondition.like("maintainer",buinessSystemSearchVO.getMaintainer()));
        }
        return  conditions;
    }

    @Override
    public Result<BuinessSystemVO> getBusiSystem(String busId) {
        BuinessSystem data = this.getOne(busId);
        if(null == data){
            return ResultUtil.success(null);
        }
        BuinessSystemVO sysVO = mapper.map(data, BuinessSystemVO.class);
        return ResultUtil.success(sysVO);
    }

    @Override
    public List<BusiAssetVO> getAssetByBusId(String busId) {
        String sql = "select asset.Guid as guid,asset.Name as name,asset.IP as ip,asset.responsible_name as responsibleName ,asset.org_name as orgName," +
                "asset_type.Name as typeName, busisystem_asset.sysdomain_guid as busiGuid ,busisystem_asset.asset_order as assetOrder" +
                " from asset " +
                "inner join busisystem_asset on asset.Guid=busisystem_asset.asset_guid " +
                "inner join asset_type on asset_type.Guid=asset.Type_Guid "+
                "where busisystem_asset.sysdomain_guid='"+busId+"' order by busisystem_asset.asset_order ";
        List<BusiAssetVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<BusiAssetVO>(BusiAssetVO.class));
        return list;
    }

    @Override
    public Result<Boolean> saveBusi(BuinessSystemSaveVO buinessSystemSaveVO) {
        BuinessSystem sysVO = mapper.map(buinessSystemSaveVO, BuinessSystem.class);
        // 校验处理
        Result<Boolean> validateResult = busiValidae(buinessSystemSaveVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateResult.getCode())){
            return validateResult;
        }
        sysVO.setGuid(UUIDUtils.get32UUID());
        sysVO.setCreateTime(new Date());
        List<SysdomainAssetVO> assets = buinessSystemSaveVO.getAssets();
        saveBusiAssets(sysVO.getGuid(),assets);
        this.save(sysVO);
        return ResultUtil.success(true);
    }

    /**
     * 校验处理
     * 1.父级业务系统、业务系统名称必填
     * 2. 业务系统名称不能重复
     * @param buinessSystemSaveVO
     * @return
     */
    private Result<Boolean> busiValidae(BuinessSystemSaveVO buinessSystemSaveVO) {
        if(StringUtils.isEmpty(buinessSystemSaveVO.getParentId())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"父级业务系统不能为空！");
        }
        if(StringUtils.isEmpty(buinessSystemSaveVO.getSysName())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统名称不能为空！");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("sysName",buinessSystemSaveVO.getSysName()));
        List<BuinessSystem> list = this.findAll(conditions);
        if(CollectionUtils.isNotEmpty(list)&&list.size()>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统名称已经存在了");
        }
        return ResultUtil.success(true);
    }

    private void saveBusiAssets(String guid, List<SysdomainAssetVO> assets) {
        if(CollectionUtils.isEmpty(assets)){
            return;
        }
        List<BuinessSystemAsset> lsits =new ArrayList<>() ;
        BuinessSystemAsset sysdomainAsset = null;
        for(SysdomainAssetVO data :assets){
            sysdomainAsset = new BuinessSystemAsset();
            sysdomainAsset.setGuid(UUIDUtils.get32UUID());
            sysdomainAsset.setAssetGuid(data.getAssetGuid());
            sysdomainAsset.setSysdomainGuid(guid);
            sysdomainAsset.setAssetOrder(data.getAssetOrder());
            lsits.add(sysdomainAsset);

        }
        if(CollectionUtils.isEmpty(lsits)){
            return;
        }
        sysdomainAssetService.save(lsits);
    }

    @Override
    public Result<Boolean> saveEdit(BuinessSystemSaveVO buinessSystemSaveVO) {
        BuinessSystem sysVO = mapper.map(buinessSystemSaveVO, BuinessSystem.class);
        if(StringUtils.isEmpty(sysVO.getGuid())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统guid不能为空");
        }
        BuinessSystem oldData= this.getOne(sysVO.getGuid());
        if(null == oldData){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"修改的数据不存在");
        }
        // 校验处理
        Result<Boolean> validateResult =  busiValidaeEdit(buinessSystemSaveVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(validateResult.getCode())){
            return validateResult;
        }
        // 清出历史数据
        String sql = "delete from busisystem_asset where  sysdomain_guid= '"+sysVO.getGuid()+"'";
        jdbcTemplate.execute(sql);
        List<SysdomainAssetVO> assets = buinessSystemSaveVO.getAssets();
        saveBusiAssets(sysVO.getGuid(),assets);
        this.save(sysVO);
        return ResultUtil.success(true);
    }

    private Result<Boolean> busiValidaeEdit(BuinessSystemSaveVO buinessSystemSaveVO) {
        if(StringUtils.isEmpty(buinessSystemSaveVO.getParentId())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"父级业务系统不能为空！");
        }
        if(StringUtils.isEmpty(buinessSystemSaveVO.getSysName())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统名称不能为空！");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("sysName",buinessSystemSaveVO.getSysName()));
        conditions.add(QueryCondition.notEq("guid",buinessSystemSaveVO.getGuid()));
        List<BuinessSystem> list = this.findAll(conditions);
        if(CollectionUtils.isNotEmpty(list)&&list.size()>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统名称已经存在了");
        }
        return ResultUtil.success(true);
    }

    /**
     * 只是删除了该业务系统与相关资产之间的关联关系
     *
     * @param buinessSystemVO
     * @return
     */
    @Override
    public Result<Boolean> deleteBusi(BuinessSystemVO buinessSystemVO) {
        if(StringUtils.isEmpty(buinessSystemVO.getGuid())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务系统guid不能为空!");
        }
        // 删除业务系统关联的资产
        String sql = "delete from busisystem_asset where  sysdomain_guid= '"+buinessSystemVO.getGuid()+"'";
        jdbcTemplate.execute(sql);
        // 删除业务系统
        this.delete(buinessSystemVO.getGuid());
        return ResultUtil.success(true);
    }

    @Override
    public Result<List<ParentBusiVO>> getParentBusi() {
        ParentBusiVO root = new ParentBusiVO();
        root.setCode(rootCode);
        root.setName(rootName);
        String sql="select guid as code,sys_name as name  from busisystem_combination";
        List<ParentBusiVO> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<ParentBusiVO>(ParentBusiVO.class));
        if(CollectionUtils.isNotEmpty(list)){
            list.add(root);
            return ResultUtil.successList(list);
        }else{
            List<ParentBusiVO> datas= new ArrayList<>();
            datas.add(root);
            return ResultUtil.successList(datas);
        }
    }


}
