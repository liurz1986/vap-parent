package com.vrv.vap.alarmdeal.business.baseauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthConfig;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthTypeConfig;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthTypeConfigRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthTypeConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.util.OptUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigVO;
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
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 审批类型配置
 *
 * 2023-08-25
 * @Author liurz
 */
@Service
@Transactional
public class BaseAuthTypeConfigServiceImpl extends BaseServiceImpl<BaseAuthTypeConfig, Integer> implements BaseAuthTypeConfigService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthTypeConfigServiceImpl.class);

    @Autowired
    private BaseAuthTypeConfigRepository baseAuthTypeConfigRepository;
    @Autowired
    private BaseAuthConfigService baseAuthConfigService;

    @Autowired
    private MapperUtil mapper;

    @Override
    public BaseRepository<BaseAuthTypeConfig, Integer> getRepository() {
        return this.baseAuthTypeConfigRepository;
    }

    /**
     * 分页查询
     * @param baseAuthTypeConfigSearchVO
     * @return
     */
    @Override
    public PageRes<BaseAuthTypeConfig> getPager(BaseAuthTypeConfigSearchVO baseAuthTypeConfigSearchVO) {
        logger.debug("审批类型配置列表查询请求参数："+ JSON.toJSONString(baseAuthTypeConfigSearchVO));
        PageReq pageReq=new PageReq();
        pageReq.setCount(baseAuthTypeConfigSearchVO.getCount_());
        pageReq.setBy("desc");
        pageReq.setOrder("id");
        pageReq.setStart(baseAuthTypeConfigSearchVO.getStart_());
        Page<BaseAuthTypeConfig> pager = findAll(pageReq.getPageable());
        List<BaseAuthTypeConfig> content = pager.getContent();
        PageRes<BaseAuthTypeConfig> pageRes =new PageRes<>();
        long totalElements = pager.getTotalElements();
        pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
        pageRes.setList(content);
        pageRes.setTotal(totalElements);
        pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        pageRes.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        return pageRes;
    }

    /**
     * 保存数据
     *
     * 1. 源对象类型、目标对象类型、标题必填，动作非必填
     * 2. 审批类型名称不能重复
     * 3. 操作类型：填后有效性校验
     * @param baseAuthTypeConfigVO
     * @return
     */
    @Override
    public Result<String> saveData(BaseAuthTypeConfigVO baseAuthTypeConfigVO) {
        logger.debug("审批类型配置保存数据请求参数："+ JSON.toJSONString(baseAuthTypeConfigVO));
        // 必填
        Result<String> isMust = isMustValidate(baseAuthTypeConfigVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(isMust.getCode())){
            return isMust;
        }
        // 审批类型名称不能重复
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("label",baseAuthTypeConfigVO.getLabel()));
        List<BaseAuthTypeConfig> typeConfigs = this.findAll(conditions);
        if(typeConfigs.size() > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型名称重复");
        }
        // 操作类型：填后有效性校验
        boolean optRes = OptUtil.isExist(baseAuthTypeConfigVO.getOpt());
        if(!optRes){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作类型不合法");
        }
        BaseAuthTypeConfig baseAuthTypeConfig = this.mapper.map(baseAuthTypeConfigVO,BaseAuthTypeConfig.class);
        baseAuthTypeConfig = this.save(baseAuthTypeConfig);
        return ResultUtil.success(baseAuthTypeConfig.getId()+"");
    }

    /**
     * 必填校验
     * 源对象类型、目标对象类型、审批类型名称必填
     * @param baseAuthTypeConfigVO
     * @return
     */
    private Result<String> isMustValidate(BaseAuthTypeConfigVO baseAuthTypeConfigVO) {
        if(StringUtils.isEmpty(baseAuthTypeConfigVO.getDstObjtype())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "目的对象类型不能为空");
        }
        if(StringUtils.isEmpty(baseAuthTypeConfigVO.getSrcObjtype())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "源对象类型不能为空");
        }
        if(StringUtils.isEmpty(baseAuthTypeConfigVO.getLabel())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "审批类型名称不能为空");
        }
        return ResultUtil.success("success");
    }

    /**
     * 数据导入
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public Result<String> importFile(MultipartFile file) throws IOException {
        Result<List<BaseAuthTypeConfig>> datas = getParseData(file);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(datas.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), datas.getMsg());
        }
        this.save(datas.getList());
        return ResultUtil.success("success");
    }

    @Override
    public Result<String> deleteByIds(List<String> idStr) {
        for(String idS : idStr){
            int id = Integer.parseInt(idS);
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("typeId",id));
            List<BaseAuthConfig> datas = baseAuthConfigService.findAll(conditions);
            if(null != datas && datas.size() >0){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),  "在审批对象中存在关联数据，不能删除！");
            }
            this.delete(id);
        }
        return ResultUtil.success("success");
    }

    /**
     * 解析数据
     * @param file
     * @return
     * @throws IOException
     */
    private Result<List<BaseAuthTypeConfig>> getParseData(MultipartFile file) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        Map<String, List<List<String>>> excelContent = ImportExcelUtil.getExcelContent(workbook);
        // 获取sheet名称为节点基础配置数据
        List<List<String>> list = excelContent.get("审批类型配置");
        List<BaseAuthTypeConfig> datas = new ArrayList<>();
        if(null == list || list.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "Excel中sheet页名称为《审批类型配置》没有数据");
        }
        for(List<String> row : list){
            BaseAuthTypeConfig bean = getBaseAuthTypeConfig(row);
            datas.add(bean);
        }
        return ResultUtil.successList(datas);
    }

    /**
     * 组装数据
     * @param row
     * @return
     */
    private BaseAuthTypeConfig getBaseAuthTypeConfig(List<String> row) {
        BaseAuthTypeConfig bean = new BaseAuthTypeConfig();
        bean.setLabel(row.get(0));
        bean.setSrcObjLabel(row.get(1));
        bean.setSrcObjtype(row.get(2));
        bean.setDstObjLabel(row.get(3));
        bean.setDstObjtype(row.get(4));
        String row5= row.get(5);
        if(StringUtils.isNotEmpty(row5)){
            bean.setOpt(Integer.parseInt(row5));
        }
        return bean;
    }

    /**
     * 下载导入模板
     * @param response
     */
    @Override
    public void downloadExportTemplate(HttpServletResponse response) {
        FileTemplateUtil.downloadExportTemplate(response,"审批类型配置导入模板");
    }
}
