package com.vrv.vap.data.controller;

import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.SearchCondition;
import com.vrv.vap.data.model.SearchTopic;
import com.vrv.vap.data.service.SearchConditionService;
import com.vrv.vap.data.service.SearchTopicService;
import com.vrv.vap.data.vo.ConditionQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/search")
@Api(value = "【搜索】相关配置 ", tags = "【搜索】相关配置")
public class SearchController extends ApiController {

    @Autowired
    SearchTopicService searchTopicService;

    @Autowired
    SearchConditionService searchConditionService;


    @ApiOperation(value = "获取全部主题")
    @GetMapping(value = "/topic")
    @SysRequestLog(description = "获取全部主题", actionType = ActionType.SELECT)
    public VData<List<SearchTopic>> topics() {
        return this.vData(searchTopicService.findAll());
    }


    @ApiOperation(value = "查询主题")
    @PostMapping(value = "/topic")
    @SysRequestLog(description = "查询主题", actionType = ActionType.SELECT,manually = false)
    public VList<SearchTopic> topics(@RequestBody Query query) {
        Example example = this.pageQuery(query, SearchTopic.class);
        return this.vList(searchTopicService.findByExample(example));
    }


    @ApiOperation(value = "新增主题")
    @PutMapping(value = "/topic")
    @SysRequestLog(description = "新增主题", actionType = ActionType.ADD)
    public VData<SearchTopic> addTopic(@RequestBody SearchTopic searchTopic) {
        int result = searchTopicService.save(searchTopic);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(searchTopic,"新增主题");
            return this.vData(searchTopic);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改主题")
    @PatchMapping(value = "/topic")
    @SysRequestLog(description = "修改主题", actionType = ActionType.UPDATE)
    public Result updateTopic(@RequestBody SearchTopic searchTopic) {
        SearchTopic topicSec = searchTopicService.findById(searchTopic.getId());
        int result = searchTopicService.updateSelective(searchTopic);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(topicSec,searchTopic,"修改主题");
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除主题 (递归删除所有子节点)")
    @DeleteMapping(value = "/topic")
    @SysRequestLog(description = "删除主题", actionType = ActionType.DELETE)
    public Result deleteTopic(@RequestBody DeleteQuery delete) {
        SearchTopic topic = searchTopicService.findById(Integer.parseInt(delete.getIds()));
        if (topic == null) {
            return this.result(false);
        }
        Set<Integer> ids = new HashSet();
        ids.add(topic.getId());
        if (topic.getType() == 1) {
            this.loop(topic, ids);
        }
        String allId = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        return this.result(searchTopicService.deleteByIds(allId) > 0);
    }


    private void loop(SearchTopic parent, Set<Integer> ids) {
        List<SearchTopic> children = searchTopicService.findByProperty(SearchTopic.class, "parentId", parent.getId());
        for (SearchTopic child : children) {
            ids.add(child.getId());
            if (child.getType() == 1) {
                this.loop(child, ids);
            }
        }
    }


    @ApiOperation(value = "新增查询条件")
    @PutMapping(value = "/condition")
    @SysRequestLog(description = "新增查询条件", actionType = ActionType.ADD)
    public VData<SearchCondition> addCondition(HttpServletRequest request, @RequestBody SearchCondition searchCondition) {

        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        searchCondition.setUserId(user.getId());

        int result = searchConditionService.save(searchCondition);
        if (result == 1) {
            SearchTopic topic = searchTopicService.findById(searchCondition.getTopicId());
            searchCondition.setTopicName(topic.getName());
            SyslogSenderUtils.sendAddSyslog(searchCondition,"新增查询条件");
            return this.vData(searchCondition);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "查询查询条件")
    @PostMapping(value = "/condition")
    @SysRequestLog(description = "查询查询条件", actionType = ActionType.SELECT)
    public VList<SearchCondition> queryCondition(HttpServletRequest request, @RequestBody ConditionQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        query.setUserId(user.getId());
        Example example = this.pageQuery(query, SearchCondition.class);
        return this.vList(searchConditionService.findByExample(example));
    }

    @ApiOperation(value = "删除查询条件")
    @DeleteMapping(value = "/condition")
    @SysRequestLog(description = "删除查询条件", actionType = ActionType.DELETE)
    public Result deleteCondition(HttpServletRequest request, @RequestBody DeleteQuery delete) {
        List<SearchCondition> searchConditions = searchConditionService.findByids(delete.getIds());
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        for (SearchCondition searchCondition : searchConditions) {
            if (searchCondition.getUserId() != user.getId()) {
                return this.result(ErrorCode.ERROR_ROLE_CONDITION);
            }
        }
        int count = searchConditionService.deleteByIds(delete.getIds());
        if (count > 0) {
            searchConditions.forEach(searchCondition -> {
                SearchTopic topic = searchTopicService.findById(searchCondition.getTopicId());
                searchCondition.setTopicName(topic.getName());
                SyslogSenderUtils.sendDeleteSyslog(searchCondition,"删除查询条件");
            });
        }
        return this.result(count > 0);
    }


}
