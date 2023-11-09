package com.vrv.vap.alarmdeal.business.asset.online.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.online.Repository.AssetOnLineRepository;
import com.vrv.vap.alarmdeal.business.asset.online.constant.AssetOnlineConstant;
import com.vrv.vap.alarmdeal.business.asset.online.model.AssetOnLine;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineService;
import com.vrv.vap.alarmdeal.business.asset.online.util.AssetOnLineExecutorServiceUtil;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineExportVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetOnLineV0;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.exportAndImport.excel.ExcelUtils;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.exportAndImport.excel.util.DateUtils;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 资产在线
 *
 * 2022-08
 */
@Service
@Transactional
public class AssetOnLineServiceImpl extends BaseServiceImpl<AssetOnLine, String> implements AssetOnLineService {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineServiceImpl.class);

    @Autowired
    private AssetOnLineRepository  assetOnLineRepository;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AssetTypeService assetTypeService;

    @Autowired
    private AssetTypeGroupService assetTypeGroupService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;





    private int batchNum = 500;  // 批量保存一次保存的最多条数

    @Override
    public BaseRepository<AssetOnLine, String> getRepository() {
        return assetOnLineRepository;
    }

    /**
     * 分页查询
     *
     * 按IP地址、发现方式、在线状态
     * @param serachAssetOnLineV0
     * @return
     */
    @Override
    public PageRes<AssetOnLineVO> query(SerachAssetOnLineV0 serachAssetOnLineV0) {
        PageReq pageReq=new PageReq();
        pageReq.setCount(serachAssetOnLineV0.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("createTime");
        pageReq.setStart(serachAssetOnLineV0.getStart_());
        PageRes<AssetOnLineVO> res = new PageRes();

        // 获取查询条件
        List<QueryCondition> conditions = searchAssetOnlineCondition(serachAssetOnLineV0);
        Page<AssetOnLine> pages =this.findAll(conditions,pageReq.getPageable());
        List<AssetOnLine> assetOnLines = pages.getContent();
        long totals = pages.getTotalElements();
        if(null == assetOnLines || assetOnLines.size() == 0){
            res.setList(new ArrayList<>());
            res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
            res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
            res.setTotal(0l);
            return res;
        }
        List<AssetOnLineVO> datas = mapper.mapList(assetOnLines,AssetOnLineVO.class);
        res.setList(datas);
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(totals);
        return res;
    }



    // 查询：按IP地址、发现方式、在线状态
    private List<QueryCondition> searchAssetOnlineCondition(SerachAssetOnLineV0 serachAssetOnLineV0) {
        List<QueryCondition> conditions = new ArrayList<>();
        //ip
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getIp())){
            conditions.add(QueryCondition.like("ip", serachAssetOnLineV0.getIp()));
        }
        // 发现方式
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getScanType())){
            conditions.add(QueryCondition.like("scanType", serachAssetOnLineV0.getScanType()));
        }
        // 在线状态
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getStatus())){
            conditions.add(QueryCondition.eq("status", serachAssetOnLineV0.getStatus()));
        }
        // 删除除外
        conditions.add(QueryCondition.notEq("isDelete", AssetOnlineConstant.ISDELETE));
        return  conditions;
    }


    /**
     * 单个删除
     * 1.删除资产在线表数据
     * 设置is_delete为-1
     * @param guid
     */
    @Override
    public void deleteByGuid(String guid) {
        // 删除资产在线数据
        AssetOnLine assetOnLine = this.getOne(guid);
        assetOnLine.setIsDelete(AssetOnlineConstant.ISDELETE);
        this.save(assetOnLine);
    }

    /**
     * 批量设置:设置后更新当前所有在线数据(针对没有导入的数据)
     * 涉及设置：
     *        资产类型(二级资产类型)， 操作系统，责任用户，责任部门
     * @param assetOnLineVO
     */
    @Override
    public Result<String> batchSetting(AssetOnLineVO assetOnLineVO) {
        // 判断当前有没有资产在线数据
        long count = this.count();
        if(count == 0){
            return ResultUtil.error(1,"当前没有在线数据，设置无效！");
        }
        // 对于资产类型更新后，重新设置对应一级资产类型
        Result<String> assetTypeHandleResult = assetTypeHandle(assetOnLineVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(assetTypeHandleResult.getCode())){
            return assetTypeHandleResult;
        }
        // 组装更新sql
        String updateSql = getUpdateSql(assetOnLineVO);
        if(null == updateSql){
            logger.info("没有更新的数据");
            return ResultUtil.error(1,"没有更新的数据");
        }
        logger.info("批量设置sql："+updateSql);
        jdbcTemplate.update(updateSql);
        return ResultUtil.success("success");
    }


    /**
     * 对于资产类型更新后，重新更新对应一级资产类型
     * @param assetOnLineVO
     */
    private Result<String> assetTypeHandle(AssetOnLineVO assetOnLineVO) {
        String typeGuid = assetOnLineVO.getTypeGuid();
        if(StringUtils.isEmpty(typeGuid)){
            return ResultUtil.success("success");
        }
        AssetType assetType = assetTypeService.getOne(typeGuid);
        if(null == assetType){
            logger.error("资产类型不存在，guid为："+typeGuid);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型不存在，guid为："+typeGuid);
        }
        assetOnLineVO.setTypeName(assetType.getName());
        String typeTreeCode = assetType.getTreeCode();
        // 获取对应的一级资产类型的treeCode
        typeTreeCode = typeTreeCode.substring(0,typeTreeCode.lastIndexOf("-"));
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("treeCode",typeTreeCode));
        List<AssetTypeGroup> groups = assetTypeGroupService.findAll(conditions);
        if(null == groups || groups.size() == 0){
            logger.error("一级资产类型不存在，二级资产类型treeCode为："+typeTreeCode);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"一级资产类型不存在，二级资产类型treeCode为："+typeTreeCode);
        }
        AssetTypeGroup typeGroup = groups.get(0);
        assetOnLineVO.setGroupName(typeGroup.getName());
        assetOnLineVO.setGroupGuid(typeGroup.getGuid());
        return ResultUtil.success("success");
    }

    /**
     * 拼接更新sql语句
     *
     * 资产类型(二级资产类型)， 操作系统，责任用户，责任部门
     * @param assetOnLineVO
     * @return
     */
    private String getUpdateSql(AssetOnLineVO assetOnLineVO) {
        String sql ="update asset_online set ";
        StringBuilder builder = new StringBuilder();
        boolean isUpdate = false;
        // 资产类型(二级资产类型)
        if(StringUtils.isNotEmpty(assetOnLineVO.getTypeGuid())){
            builder.append("group_name='"+assetOnLineVO.getGroupName()+"',");
            builder.append("group_guid='"+assetOnLineVO.getGroupGuid()+"',");
            builder.append("type_name='"+assetOnLineVO.getTypeName()+"',");
            builder.append("type_guid='"+assetOnLineVO.getTypeGuid()+"',");
            isUpdate = true;
        }
        // 操作系统
        if(StringUtils.isNotEmpty(assetOnLineVO.getOs())){
            builder.append("os='"+assetOnLineVO.getOs()+"',");
            isUpdate = true;
        }
        // 责任用户
        if(StringUtils.isNotEmpty(assetOnLineVO.getResponsibleCode())){
            builder.append("person_code='"+assetOnLineVO.getResponsibleCode()+"',");
            builder.append("person_name='"+assetOnLineVO.getResponsibleName()+"',");
            isUpdate = true;
        }
        // 责任部门
        if(StringUtils.isNotEmpty(assetOnLineVO.getOrgCode())){
            builder.append("org_code='"+assetOnLineVO.getOrgCode()+"',");
            builder.append("org_name='"+assetOnLineVO.getOrgName()+"',");
            isUpdate = true;
        }
        if(isUpdate){
            String builderstr = builder.toString();
            builderstr = builderstr.substring(0,builderstr.lastIndexOf(","));
            sql = sql + builderstr;
            sql = sql +" where is_import='0'";
            return  sql;
        }else{ // 没有变动不做更新
            return null;
        }
    }

    /**
     * 单条数据导入台账：数据同步asset表中、操作系统录入到asset_extend
     *
     * 条件：
     *  1. ip必填，是不是已经入过库，是不是在资产表中存在
     *  2. 责任人及责任单位必填，有效性
     *  3. 资产大类、小类必填，有效性
     *
     * @param assetOnLineVO
     */
    @Override
    public Result<String> writeAsset(AssetOnLineVO assetOnLineVO) {
        // 判断入台账数据是否存在
        AssetOnLine assetOnline = this.getOne(assetOnLineVO.getGuid());
        if(null == assetOnline){
            return ResultUtil.error(1,"当前数据不存在！");
        }
        // 必填性校验
        Result<String> isMushResult = isMust(assetOnLineVO);
        if(!ResultCodeEnum.SUCCESS.getCode().equals(isMushResult.getCode())){
            return isMushResult;
        }
        // ip校验
        Result<String> ipResult =ipValidate(assetOnLineVO);
        if(!ResultCodeEnum.SUCCESS.getCode().equals(ipResult.getCode())){
            return ipResult;
        }
        // 有效性校验
        Result<String> dataValidateResult = dataValidate(assetOnLineVO);
        if(!ResultCodeEnum.SUCCESS.getCode().equals(dataValidateResult.getCode())){
            return dataValidateResult;
        }
        // 组装asset对象
        Asset asset = getAssetByAssetOnLine(assetOnLineVO);
        // 入台账
        assetService.save(asset);
        // 入台账成功，更新入台账记录数据
        assetOnline= getUpdateAssetOnline(assetOnLineVO,assetOnline);
        this.save(assetOnline);
        return ResultUtil.success("success");
    }

    private AssetOnLine getUpdateAssetOnline(AssetOnLineVO assetOnLineVO, AssetOnLine assetOnline) {
        assetOnline.setIsImport(AssetOnlineConstant.IMPORTASSET);
        assetOnline.setIp(assetOnLineVO.getIp());
        assetOnline.setGroupGuid(assetOnLineVO.getGroupGuid());
        assetOnline.setGroupName(assetOnLineVO.getGroupName());
        assetOnline.setTypeName(assetOnLineVO.getTypeName());
        assetOnline.setTypeGuid(assetOnLineVO.getTypeGuid());
        assetOnline.setOrgCode(assetOnLineVO.getOrgCode());
        assetOnline.setOrgName(assetOnLineVO.getOrgName());
        assetOnline.setResponsibleCode(assetOnLineVO.getResponsibleCode());
        assetOnline.setResponsibleName(assetOnLineVO.getResponsibleName());
        assetOnline.setOs(assetOnLineVO.getOs());
        return assetOnline;
    }

    /**
     * 责任人有效性
     *  资产大类、小类有效性
     * @param assetOnLineVO
     * @return
     */
    private Result<String> dataValidate(AssetOnLineVO assetOnLineVO) {
        // 资产类型校验
        Result<String> assetTypeValidateResult=  assetTypeValidate(assetOnLineVO);
        if(!ResultCodeEnum.SUCCESS.getCode().equals(assetTypeValidateResult.getCode())){
            return assetTypeValidateResult;
        }
        // 责任人校验
        Result<String> responsibleNameAndOrgNameValidateResult= responsibleNameAndOrgNameValidate(assetOnLineVO);
        if(!ResultCodeEnum.SUCCESS.getCode().equals(responsibleNameAndOrgNameValidateResult.getCode())){
            return responsibleNameAndOrgNameValidateResult;
        }
        return ResultUtil.success("SUCCESS");
    }

    private Result<String> responsibleNameAndOrgNameValidate(AssetOnLineVO assetOnLineVO) {
        String responsibleCode = assetOnLineVO.getResponsibleCode();
        List<BasePersonZjg> basePersonZjgList = assetBaseDataCacheService.queryAllPerson();
        BasePersonZjg data = getPersonByCode(responsibleCode,basePersonZjgList);
        if(null == data){
            return ResultUtil.error(1,"责任用户不存在："+responsibleCode);
        }
        assetOnLineVO.setResponsibleName(data.getUserName());
        assetOnLineVO.setOrgCode(data.getOrgCode());
        assetOnLineVO.setOrgName(data.getOrgName());
        return ResultUtil.success("SUCCESS");
    }

    private BasePersonZjg getPersonByCode(String userNo, List<BasePersonZjg> basePersonZjgList) {
        for (BasePersonZjg zig : basePersonZjgList) {
            if (userNo.equalsIgnoreCase(zig.getUserNo())) {
                return zig;
            }
        }
        return null;
    }

    private Result<String> assetTypeValidate(AssetOnLineVO assetOnLineVO) {
        String typeGuid = assetOnLineVO.getTypeGuid();
        AssetType assetType = assetTypeService.getOne(typeGuid);
        if(null  == assetType){
            return ResultUtil.error(1,"资产小类不存在，当前资产小类guid："+typeGuid);
        }
        assetOnLineVO.setTypeName(assetType.getName());
        String groupGuid = assetOnLineVO.getGroupGuid();
        AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(groupGuid);
        if(null  == assetTypeGroup){
            return ResultUtil.error(1,"资产大类不存在，当前资产小类guid："+groupGuid);
        }
        assetOnLineVO.setGroupName(assetTypeGroup.getName());
        return ResultUtil.success("success");
    }

    /**
     *判断ip是不是已经入过库
     * 判断ip是不是在asset表中
     * @param assetOnLineVO
     * @return
     */
    private Result<String> ipValidate(AssetOnLineVO assetOnLineVO) {
        String ip = assetOnLineVO.getIp();
        // ip格式校验
        boolean checkIPResult = AssetUtil.checkIP(ip);
        if(!checkIPResult){
            return ResultUtil.error(1, "ip格式异常");
        }
        // 判断ip是不是已经入过库
        if(AssetOnlineConstant.IMPORTASSET.equals(assetOnLineVO.getIsImport())){
            return ResultUtil.error(1,"该ip已经执行过入台账操作");
        }
        // 判断ip是不是在asset表中
        boolean ipExist = ipExistAsset(ip);
        if(ipExist){
            return ResultUtil.error(1,"该ip在台账表中存在！");
        }
        return ResultUtil.success("SUCCESS");
    }

    /**
     * 必填校验
     * @param assetOnLineVO
     * @return
     */
    private Result<String> isMust(AssetOnLineVO assetOnLineVO) {
        String ip = assetOnLineVO.getIp();
        // ip不能为空
        if(StringUtils.isEmpty(ip)){
            return ResultUtil.error(1,"ip为不能为空！");
        }
        // 资产小类
        if(StringUtils.isEmpty(assetOnLineVO.getTypeGuid())||StringUtils.isEmpty(assetOnLineVO.getTypeName())){
            return ResultUtil.error(1,"资产小类不能为空！");
        }
        // 资产大类
        if(StringUtils.isEmpty(assetOnLineVO.getGroupGuid())||StringUtils.isEmpty(assetOnLineVO.getGroupName())){
            return ResultUtil.error(1,"资产大类不能为空！");
        }
        // 责任用户
        if(StringUtils.isEmpty(assetOnLineVO.getResponsibleCode())||StringUtils.isEmpty(assetOnLineVO.getResponsibleName())){
            return ResultUtil.error(1,"责任用户不能为空！");
        }
        // 归属单位
        if(StringUtils.isEmpty(assetOnLineVO.getOrgCode())||StringUtils.isEmpty(assetOnLineVO.getOrgName())){
            return ResultUtil.error(1,"归属单位不能为空！");
        }
        return ResultUtil.success("success");
    }


    /**
     * 组装asset对象
     * @param assetOnLineVO
     * @return
     */
    private Asset getAssetByAssetOnLine(AssetOnLineVO assetOnLineVO) {
        Asset asset = new Asset();
        asset.setGuid(UUIDUtils.get32UUID());
        asset.setName(assetOnLineVO.getName());
        asset.setIp(assetOnLineVO.getIp());
        asset.setAssetType(assetOnLineVO.getTypeGuid());
        asset.setOrgCode(assetOnLineVO.getOrgCode());
        asset.setOrgName(assetOnLineVO.getOrgName());
        asset.setResponsibleCode(assetOnLineVO.getResponsibleCode());
        asset.setResponsibleName(assetOnLineVO.getResponsibleName());
        asset.setEmployeeCode1(assetOnLineVO.getResponsibleCode());
        asset.setOsList(assetOnLineVO.getOs());
        asset.setCreateTime(new Date());
        // 资产价值五权设置默认值
        asset.setSecrecy("0");
        asset.setIntegrity("0");
        asset.setImportance("0");
        asset.setLoadBear("0");
        asset.setWorth("0");
        // 增加一个类型 2023-1-4
        asset.setDataSourceType(4); // 探针发现
        return asset;
    }


    /**
     * 判断ip是不是在asset表中
     * @param ip
     * @return
     */
    private boolean ipExistAsset(String ip) {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("ip",ip));
        List<Asset> assets =assetService.findAll(conditions);
        if(null != assets && assets.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public Result<String> exportAssetOnLineInfo(SerachAssetOnLineV0 serachAssetOnLineV0) {
        String uuid = "资产在线" + DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
        String fileName = uuid + ".xls";
        String rootPath = fileConfiguration.getAssetOnLinePath();
        File targetFile = new File(rootPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String filePath = Paths.get(rootPath, fileName).toString();
        try {
            // 获取导出数据
            List<AssetOnLineExportVO> datas = getAssetLineExportDatas(serachAssetOnLineV0);
            ExcelUtils.getInstance().exportObjects2Excel(datas, AssetOnLineExportVO.class, true, filePath);
            return ResultUtil.success(uuid);
        } catch (ExcelException | IOException e) {
            logger.error("导出excel异常", e);
            return ResultUtil.error(-1,"导出excel异常");
        }
    }

    /**
     * 获取导出数据
     * @param serachAssetOnLineV0
     * @return
     */
    private List<AssetOnLineExportVO> getAssetLineExportDatas(SerachAssetOnLineV0 serachAssetOnLineV0) {
        String sql = getQueryExportSql(serachAssetOnLineV0);
        List<AssetOnLineExportVO> details = jdbcTemplate.query(sql, new AllAssetSyncMapper());
        return details;

    }

    /**
     * 组装数据，同时进行状态、标记转换处理
     */
    public class AllAssetSyncMapper implements RowMapper<AssetOnLineExportVO>{
        @Override
        public AssetOnLineExportVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetOnLineExportVO asset = new AssetOnLineExportVO();
            asset.setName(rs.getString("name"));
            asset.setIp(rs.getString("ip"));
            asset.setGroupName(rs.getString("groupName"));
            asset.setTypeName(rs.getString("typeName"));
            asset.setStatus(transferStatus(rs.getString("status")));
            asset.setOs(rs.getString("os"));
            asset.setScanType(rs.getString("scanType"));
            asset.setOrgName(rs.getString("orgName"));
            asset.setResponsibleName(rs.getString("responsibleName"));
            asset.setCurTime(rs.getTimestamp("curTime"));
            asset.setFirstTime(rs.getTimestamp("firstTime"));
            asset.setFlage(transferFlage(rs.getString("flag")));
            return asset;
        }
    }

    // 状态转换：在线、离线
    private String transferStatus(String status) {
        if(AssetOnlineConstant.ASSETONLINE.equals(status)){
           return AssetOnlineConstant.ASSETONLINEVALUE;
        }
        if(AssetOnlineConstant.NOASSETONLINE.equals(status)){
            return AssetOnlineConstant.NOASSETONLINEVALUE;
        }
        return ""; // 没有匹配的默认为空
    }

    // 标记转换处理：有值表示导入"0表示"没有导入 “1”表示导入
    private String transferFlage(String flag) {
        if(AssetOnlineConstant.IMPORTASSET.equals(flag)){
            return AssetOnlineConstant.IMPORTASSETVALUE;
        }
        return AssetOnlineConstant.NOIMPORTASSETVALUE;
    }

    /**
     * 获取导出数据sql
     * @param serachAssetOnLineV0
     * @return
     */
    private String getQueryExportSql(SerachAssetOnLineV0 serachAssetOnLineV0) {
        String sql = "select a.name,a.ip,a.type_name as typeName,a.group_name as groupName,a.status,a.os,a.scan_type as scanType,a.person_name as responsibleName,a.org_name as orgName,a.first_time as firstTime,a.cur_time as curTime,a.is_import as flag " +
                " from asset_online as a  where 1 = 1";
        // ip地址
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getIp())){
            sql = sql +" and  a.ip like '%"+serachAssetOnLineV0.getIp()+"%'";
        }
        // 发现方式
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getScanType())){
            sql = sql +" and  a.scan_type like '%"+serachAssetOnLineV0.getScanType()+"%'";
        }
        // 在线状态
        if(StringUtils.isNotEmpty(serachAssetOnLineV0.getStatus())){
            sql = sql +" and  a.status='"+serachAssetOnLineV0.getStatus()+"'";
        }
        sql = sql + " order by a.create_time desc" ;
        return sql;
    }

    @Override
    public void exportAssetOnLineFile(String fileName, HttpServletResponse response) {
        String realPath = fileConfiguration.getAssetOnLinePath();
        ; // 文件路径
        FileUtil.downLoadFile(fileName + ".xls", realPath, response);


    }

    /**
     * 批量保存数据
     * @param onlins
     */
    @Override
    public void batchSaveDatas(List<AssetOnLine> onlins) {
        // 500保存一次
        if (onlins.size() <= batchNum) {
            this.save(onlins);
        } else {
            List<AssetOnLine> saveDatas = onlins.subList(0, batchNum);
            this.save(saveDatas);
            onlins.removeAll(saveDatas);
            batchSaveDatas(onlins);
        }
    }

    /**
     * 获取所有资产的ip及资产类型
     * @return
     */
    @Override
    public Future<List<AssetQueryVO>> getAllAssetsFuture()  {
        Future<List<AssetQueryVO>> future = AssetOnLineExecutorServiceUtil.getAssetOnLinePool().submit(new Callable() {
            @Override
            public List<AssetQueryVO> call() throws Exception {
                return getAllAssets();
            }
        });
        return future;
    }

    /**
     * 获取所有资产的ip及资产类型
     * @return
     */
    private List<AssetQueryVO> getAllAssets() {
        String sql = "select asset.ip ,asset.type_guid ,asType.name as typeName from asset inner join asset_type as asType on asset.Type_Guid=asType.Guid" +
                     " where asset.ip is not null and asset.ip !=''";
        List<AssetQueryVO> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetQueryVO>(AssetQueryVO.class));
        return assets;
    }

}
