package com.vrv.vap.alarmdeal.business.alaramevent.strategy.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RiskRuleInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RuleOperation;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterPagerVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月07日 11:25
 */
public interface RuleService {

    RiskRuleInfo getRiskRuleInfo();
    /**
     * 查询规则运行情况
     * @return
     */
    RuleOperation getRuleOperation();

    /**
     * 删除规则
     * @param id
     * @return
     */
    Boolean deleteRuleForId(String id);

    /**
     * 规则文件导入
     * @param file
     * @return
     */
    List<String> importRule(MultipartFile file);

    /**
     * 导出规则文件
     * @param filterPagerVO
     * @return
     */
    String exportRule(FilterPagerVO filterPagerVO, HttpServletResponse httpServletResponse);

    String getFilePath();
}
