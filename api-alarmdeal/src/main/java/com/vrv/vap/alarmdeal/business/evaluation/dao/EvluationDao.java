package com.vrv.vap.alarmdeal.business.evaluation.dao;

import com.vrv.vap.alarmdeal.business.evaluation.vo.ConifgTreeVO;
import com.vrv.vap.alarmdeal.business.evaluation.vo.EvaluationReportSearchVO;
import com.vrv.vap.alarmdeal.business.evaluation.vo.KeyValueVO;
import com.vrv.vap.alarmdeal.business.evaluation.vo.NoInforOrgVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 自查自评
 *
 * @Date 2023-09
 * @author liurz
 */
@Repository
public class EvluationDao {
    private static Logger logger = LoggerFactory.getLogger(EvluationDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<ConifgTreeVO> getTree() {
        String sql = "select distinct check_type  as checkType from self_inspection_evaluation_config";
        List<ConifgTreeVO> result =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<ConifgTreeVO>(ConifgTreeVO.class));
        return result;
    }

    public Map<String, Object> statusStatistics() {
       String sql="select (select count(*) from self_inspection_evaluation )as total,(select count(*) from self_inspection_evaluation where status =0 )as status0,(select count(*) from self_inspection_evaluation where status =1)as status1";
       return jdbcTemplate.queryForMap(sql);
    }

    public List<Map<String, Object>> depAndGeneticStatistics() {
        String sql="select genetic_type as geneticType,org_name as orgName,count(*) as number from self_inspection_evaluation group by genetic_type,org_name";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 删除关联中间表信息
     * @param ids
     */
    public void deleteProcessIds(List<String> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        String sql="delete from self_inspection_evaluation_process where id in ('" + StringUtils.join(ids, "','") + "')";
        jdbcTemplate.execute(sql);
    }

    /**
     * 报告： 自查自评状态统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<KeyValueVO> queryStatusStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql="select count(*) as num ,'未完成' as name from self_inspection_evaluation where status =0  and create_time between  ? and  ? "  +
                "union " +
                "select count(*) as num ,'已完成' as name from self_inspection_evaluation where status =1 and create_time between  ? and  ? ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<KeyValueVO>(KeyValueVO.class),new Object[]{startTime,endTime,startTime,endTime});
    }
    /**
     * 报告：成因类型分类统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<KeyValueVO> queryGeneticTypeStatistics(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql = "select genetic_type as name ,count(*) as num  from self_inspection_evaluation  where  create_time between  ? and  ?  group by genetic_type ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<KeyValueVO>(KeyValueVO.class),new Object[]{startTime,endTime});
    }

    /**
     * 检查大类监管事件统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<KeyValueVO> queryEventByCheckType(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql = "select check_type as name, sum(occur_count) as num from self_inspection_evaluation  where  create_time between  ? and  ?  group by check_type ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<KeyValueVO>(KeyValueVO.class),new Object[]{startTime,endTime});
    }

    /**
     * 成因类型监管事件统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<KeyValueVO> queryEventByGeneticType(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql = "select genetic_type as name, sum(occur_count) as num from self_inspection_evaluation  where  create_time between  ? and  ?  group by genetic_type ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<KeyValueVO>(KeyValueVO.class),new Object[]{startTime,endTime});
    }

    /**
     * 信息化工作机构成因类型事件统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<KeyValueVO> queryEventByInforOrg(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql = "select genetic_type as name, sum(occur_count)as num  from self_inspection_evaluation  where  org_name='信息化工作机构' and create_time between  ? and  ?  group by genetic_type ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<KeyValueVO>(KeyValueVO.class),new Object[]{startTime,endTime});
    }

    /**
     * 非信息化工作机构成因类型事件统计
     * @param evaluationReportSearchVO
     * @return
     */
    public List<NoInforOrgVO> queryEventByNoInforOrg(EvaluationReportSearchVO evaluationReportSearchVO) {
        Date endTime = evaluationReportSearchVO.getEndTime();
        Date startTime = evaluationReportSearchVO.getStartTime();
        String sql = "select org_name as orgName,genetic_type as geneticType,count(*) as num  from self_inspection_evaluation where org_name !='信息化工作机构' and create_time between  ? and  ?  group by org_name,genetic_type  ";
        return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<NoInforOrgVO>(NoInforOrgVO.class),new Object[]{startTime,endTime});
    }
}
