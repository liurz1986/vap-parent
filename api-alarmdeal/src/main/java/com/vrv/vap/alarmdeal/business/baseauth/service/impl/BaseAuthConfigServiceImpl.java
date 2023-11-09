package com.vrv.vap.alarmdeal.business.baseauth.service.impl;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthConfig;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthTypeConfig;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthConfigRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthTypeConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.util.OptUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigVO;
import com.vrv.vap.alarmdeal.frameworks.util.FileTemplateUtil;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 审批信息处理
 *
 * 2023-08-25
 * @author liurz
 */
@Service
@Transactional
public class BaseAuthConfigServiceImpl extends BaseServiceImpl<BaseAuthConfig, Integer> implements BaseAuthConfigService {

    private static Logger logger = LoggerFactory.getLogger(BaseAuthConfigServiceImpl.class);
    @Autowired
    private BaseAuthConfigRepository baseAuthConfigRepository;
    @Autowired
    private BaseAuthTypeConfigService baseAuthTypeConfigService;
    @Autowired
    private MapperUtil mapper;
    @Override
    public BaseRepository<BaseAuthConfig, Integer> getRepository() {
        return this.baseAuthConfigRepository;
    }

    /**
     * 分页查询
     * @param baseAuthConfigSearchVO
     * @return
     */
    @Override
    public PageRes<BaseAuthConfig> getPager(BaseAuthConfigSearchVO baseAuthConfigSearchVO) {
        logger.debug("审批类型列表查询请求参数："+ JSON.toJSONString(baseAuthConfigSearchVO));
        Integer typeGuid = baseAuthConfigSearchVO.getTypeId();
        PageRes<BaseAuthConfig> result = new PageRes<>();
        if(null == baseAuthConfigSearchVO.getTypeId()){
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            result.setMessage("审批信息配置的ID不能为空");
            return result;
        }
        BaseAuthTypeConfig typeConfig = baseAuthTypeConfigService.getOne(typeGuid);
        if(null == typeConfig){
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            result.setMessage("审批信息配置的ID对应的数据不存在，配置的ID"+typeGuid);
            return result;
        }
        PageReq pageReq=new PageReq();
        pageReq.setCount(baseAuthConfigSearchVO.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("id");
        pageReq.setStart(baseAuthConfigSearchVO.getStart_());
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("typeId",typeGuid));
        Page<BaseAuthConfig> pager = findAll(conditions,pageReq.getPageable());
        List<BaseAuthConfig> content = pager.getContent();
        PageRes<BaseAuthConfig> pageRes =new PageRes<>();
        long totalElements = pager.getTotalElements();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setList(content);
        pageRes.setTotal(totalElements);
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        pageRes.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        return pageRes;
    }

    /**
     * 数据保存
     * 1. 源对象名称、源对象标识、目的对象名称、目的对象标识、审批类型ID必填校验
     * 2. 审批类型ID有效性校验
     * 3. 源对象标识+目的对象标识+审批类型ID 唯一性校验
     * 4. 操作类型：填后有效性校验
     * @param baseAuthConfigVO
     * @return
     */
    @Override
    public Result<String> saveDate(BaseAuthConfigVO baseAuthConfigVO) {
        logger.debug("审批类型数据保存请求参数："+ JSON.toJSONString(baseAuthConfigVO));
        // 必填校验
        Result<String> result = isMustValidate(baseAuthConfigVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return result;
        }
        // 审批类型ID有效性校验
        Integer typeId = baseAuthConfigVO.getTypeId();
        BaseAuthTypeConfig baseAuthTypeConfig = this.baseAuthTypeConfigService.getOne(typeId);
        if(null == baseAuthTypeConfig){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型ID对应审批类型配置信息不存在");
        }
        // 操作类型：填后有效性校验
        boolean optRes = OptUtil.isExist(baseAuthTypeConfig.getOpt());
        if(!optRes){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作类型不合法");
        }
        BaseAuthConfig baseAuthConfig = this.mapper.map(baseAuthConfigVO,BaseAuthConfig.class);
        baseAuthConfig.setCreateTime(new Date());
        baseAuthConfig = this.save(baseAuthConfig);
        return ResultUtil.success(baseAuthConfig.getId()+"");
    }
    /**
     * 必填校验
     * 源对象标识、目的对象标识、审批类型ID
     * @param baseAuthConfigVO
     * @return
     */
    private Result<String> isMustValidate(BaseAuthConfigVO baseAuthConfigVO) {
        if(StringUtils.isEmpty(baseAuthConfigVO.getSrcObj())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "源对象标识不能为空");
        }
        if(StringUtils.isEmpty(baseAuthConfigVO.getDstObj())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "目的对象标识不能为空");
        }
        if(null == baseAuthConfigVO.getTypeId()||baseAuthConfigVO.getTypeId() <=0 ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型ID不合法");
        }
        return ResultUtil.success("success");
    }

    /**
     * 编辑数据
     *
     * @param baseAuthConfigVO
     * @return
     */
    @Override
    public Result<String> saveEdit(BaseAuthConfigVO baseAuthConfigVO) {
        logger.debug("审批类型数据编辑请求参数："+ JSON.toJSONString(baseAuthConfigVO));
        BaseAuthConfig baseAuthConfigOld = this.getOne(baseAuthConfigVO.getId());
        if(null == baseAuthConfigOld){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "编辑数据不存在,id为："+baseAuthConfigVO.getId());
        }
        // 必填校验
        Result<String> result = isMustValidate(baseAuthConfigVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return result;
        }
        Integer typeId = baseAuthConfigVO.getTypeId();
        BaseAuthTypeConfig baseAuthTypeConfig = this.baseAuthTypeConfigService.getOne(typeId);
        if(null == baseAuthTypeConfig){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型ID对应审批类型配置信息不存在");
        }
        // 操作类型：填后有效性校验
        boolean optRes = OptUtil.isExist(baseAuthTypeConfig.getOpt());
        if(!optRes){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作类型不合法");
        }
        BaseAuthConfig baseAuthConfig = this.mapper.map(baseAuthConfigVO,BaseAuthConfig.class);
        baseAuthConfig.setCreateTime(baseAuthConfigOld.getCreateTime());
        this.save(baseAuthConfig);
        return ResultUtil.success("success");
    }

    /**
     * 导入解析入库
     * 目前只解析了然后直接入库
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public Result<String> importFile(MultipartFile file) throws IOException {
        Result<List<BaseAuthConfig>> datas = getParseData(file);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(datas.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), datas.getMsg());
        }
        this.save(datas.getList());
        return ResultUtil.success("success");
    }

    /**
     * 解析数据
     * @param file
     * @return
     */
    private Result<List<BaseAuthConfig>> getParseData(MultipartFile file) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        Map<String, List<List<String>>> excelContent = ImportExcelUtil.getExcelContent(workbook);
        // 获取sheet名称为审批信息
        List<List<String>> list = excelContent.get("审批信息");
        List<BaseAuthConfig> datas = new ArrayList<>();
        if(null == list || list.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "Excel中sheet页名称为《审批信息》没有数据");
        }
        for(List<String> row : list){
            BaseAuthConfig bean = getBaseAuthConfig(row);
            datas.add(bean);
        }
         return ResultUtil.successList(datas);
    }

    /**
     * 组装数据
     * @param row
     * @return
     */
    private BaseAuthConfig getBaseAuthConfig(List<String> row) {
        BaseAuthConfig bean = new BaseAuthConfig();
        bean.setSrcObjLabel(row.get(0));
        bean.setSrcObj(row.get(1));
        bean.setDstObjLabel(row.get(2));
        bean.setDstObj(row.get(3));
        String row4 = row.get(4);
        if(StringUtils.isNotEmpty(row4)){
            bean.setTypeId(Integer.parseInt(row4));
        }
        String optType = row.get(5);
        if(StringUtils.isNotEmpty(optType)){
            bean.setOpt(Integer.parseInt(optType));
        }
        bean.setCreateTime(new Date());
        return bean;
    }
    /**
     * 下载导入模板
     * @param response
     */
    @Override
    public void downloadExportTemplate(HttpServletResponse response) {
        FileTemplateUtil.downloadExportTemplate(response,"审批信息导入模板");
    }
}
