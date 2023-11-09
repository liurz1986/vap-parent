package com.vrv.vap.admin.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.DiscoverRecordMapper;
import com.vrv.vap.admin.model.DiscoverRecord;
import com.vrv.vap.admin.service.DiscoverRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/03/23.
 */
@Service
@Transactional
public class DiscoverRecordServiceImpl extends BaseServiceImpl<DiscoverRecord> implements DiscoverRecordService {
    @Resource
    private DiscoverRecordMapper discoverRecordMapper;

    @Override
    public boolean saveRecord(DiscoverRecord record) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String inputValue = record.getSearchKey();
        final String indexId = record.getIndexId();
        final String startTime = record.getStartTime();
        final String endTime = record.getEndTime();

        //获取查询字段
        HttpSession session = request.getSession();
        if (session.getAttribute(Global.SESSION.USER) != null && StringUtils.isNotEmpty(inputValue)) {
            User userinfo = (User) session.getAttribute(Global.SESSION.USER);
            //近一天是否已查询过该记录，有则更新时间
            Example example = new Example(DiscoverRecord.class);
            example.orderBy("searchTime").desc();
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("account", userinfo.getAccount());
            criteria.andEqualTo("indexId", indexId);
            criteria.andEqualTo("searchKey", inputValue);
            if (StringUtils.isNotEmpty(startTime)) {
                criteria.andEqualTo("startTime", startTime);
            }
            if (StringUtils.isNotEmpty(endTime)) {
                criteria.andEqualTo("endTime", endTime);
            }
            criteria.andGreaterThan("searchTime", TimeTools.getNowBeforeByDay(0));
            List<DiscoverRecord> list = discoverRecordMapper.selectByExample(example);
            if (list != null && list.size() > 0) {
                DiscoverRecord discoverRecord = list.get(0);
                discoverRecord.setSearchTime(new Date());
                discoverRecordMapper.updateByPrimaryKey(discoverRecord);
                return true;
            }
            if (StringUtils.isNotEmpty(startTime) || (StringUtils.isNotEmpty(endTime))) {
                //限制总记录数
                this.limitRecordCount(userinfo);
            }
            record.setAccount(userinfo.getAccount());
            record.setRoleId(!CollectionUtils.isEmpty(userinfo.getRoleCode()) ? Arrays.toString(userinfo.getRoleCode().toArray()) : "");
            record.setSearchTime(new Date());
            discoverRecordMapper.insertSelective(record);
            return true;
        }
        return false;
    }

    /**
     * 限制总记录数，到达500条先删除时间最旧的一条
     *
     * @param userinfo
     */
    public void limitRecordCount(User userinfo) {
        //近一天是否已查询过该记录，有则更新时间
        Example example = new Example(DiscoverRecord.class);
        example.orderBy("searchTime").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account", userinfo.getAccount());
        List<DiscoverRecord> list = discoverRecordMapper.selectByExample(example);
        if (list != null && list.size() >= 500) {
            DiscoverRecord discoverRecord = list.get(0);
            discoverRecordMapper.deleteByPrimaryKey(discoverRecord.getId());
        }
    }
}
