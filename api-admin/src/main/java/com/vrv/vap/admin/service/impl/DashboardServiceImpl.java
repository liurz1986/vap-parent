package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.mapper.DashboardMapper;
import com.vrv.vap.admin.mapper.VisualWidgetMapper;
import com.vrv.vap.admin.model.Dashboard;
import com.vrv.vap.admin.model.VisualWidgetModel;
import com.vrv.vap.admin.service.DashboardService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class DashboardServiceImpl extends BaseServiceImpl<Dashboard> implements DashboardService {

    @Resource
    private DashboardMapper dashboardMapper;

    @Resource
    private VisualWidgetMapper widgetMapper;

    @Override
    public void cancelFirstPage() {
        dashboardMapper.cancelFirstPage();
    }

	@Override
	public int delUnmatchedWidgets(List<Dashboard> DashboardList) {
		StringBuffer  idsBuf=new StringBuffer();
		int result=1;
		List<String> ids=new ArrayList<String>();
		List<VisualWidgetModel> widgetsList=widgetMapper.selectAll();
		Set<String> dashboardIds=new HashSet<String>();
		for(Dashboard  dashboard: DashboardList){
		    String uiStateJSON = dashboard.getUiStateJSON();
		    if(StringUtils.isNotEmpty(uiStateJSON)){
			    JSONArray uiStateArray = JSONArray.fromObject(uiStateJSON);
			    for(int i=0;i<uiStateArray.size();i++){
				   String  id = uiStateArray.getJSONObject(i).get("id").toString();
		           dashboardIds.add(id);
			    }
		    }
		}
		for(VisualWidgetModel  widget:widgetsList){
			if(!dashboardIds.contains(widget.getId())){
				ids.add(widget.getId());
			}
		}
		int count=0;
		for(String id:ids){
			if(count==ids.size()-1){
				idsBuf.append(id);
			}else{
				idsBuf.append(id+",");
			}
			++count;
		}
		if(StringUtils.isNotEmpty(idsBuf.toString())){
			result=widgetMapper.deleteByIds(idsBuf.toString());
		}
		return result;
	}

	@Override
	public Result linkShareLogin(HttpServletRequest request, String account, String password) {
		RestTemplate restTemplate = new RestTemplate();
		String strURL = request.getScheme() + "://" + request.getServerName() + "/api-common/user/login";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			value = CommonTools.filterHeaderSymbol(value);
			requestHeaders.add(CleanUtil.cleanString(key), CleanUtil.cleanString(value));
		}
		requestHeaders.add("SESSION", request.getSession().getId());
		String userStr="{\"uuu\":\""+account+"\","+"\"ppp\":\""+CommonTools.string2MD5(password)+"\"}";
		HttpEntity<String> requestEntity = new HttpEntity<String>(userStr, requestHeaders);
		Result result = restTemplate.postForObject(strURL, requestEntity, Result.class);
		return result;
	}

	@Override
	public int updateTopDashboard(Dashboard dashboard) {
    	Dashboard db = dashboardMapper.selectByPrimaryKey(dashboard.getId());
    	if (db == null) {
    		return 0;
		}
		Integer max = dashboardMapper.findMaxTop();
		db.setTop(max);
		int result = dashboardMapper.updateByPrimaryKeySelective(db);
		return result;
	}
}
