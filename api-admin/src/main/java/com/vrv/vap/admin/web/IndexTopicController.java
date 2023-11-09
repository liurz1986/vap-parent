package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.model.IndexTopic;
import com.vrv.vap.admin.service.IndexTopicService;
import com.vrv.vap.admin.vo.IndexTopicQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@RestController
@RequestMapping(path = "/topic")
public class IndexTopicController extends ApiController {

    @Autowired
    IndexTopicService indexTopicService;


    @GetMapping
    @ApiOperation("获取所有主题")
    @SysRequestLog(description = "获取所有主题",actionType = ActionType.SELECT)
    public Result getTopics(){
            return  this.vData(indexTopicService.findAll());
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @PutMapping
    @ApiOperation("添加主题")
    @SysRequestLog(description = "添加主题",actionType = ActionType.ADD)
    public Result addTopic(@RequestBody  IndexTopic indexTopic){
        indexTopic.setStatus("01");//默认设置为可用
        int r = indexTopicService.save(indexTopic);
        indexTopicService.setDefaultTopic();
        return this.result(r==1);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @PatchMapping
    @ApiOperation("修改主题")
    @SysRequestLog(description = "修改主题",actionType = ActionType.UPDATE)
    public Result updateTopic(@RequestBody  IndexTopic indexTopic){
        int r = indexTopicService.updateSelective(indexTopic);
        indexTopicService.setDefaultTopic();
        return this.result(r==1);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @DeleteMapping
    @ApiOperation("删除主题")
    @SysRequestLog(description = "删除主题",actionType = ActionType.DELETE)
    public Result deleteTopic(@RequestBody  IndexTopic indexTopic){
        int r = indexTopicService.deleteById(indexTopic.getId());
        indexTopicService.setDefaultTopic();
        return this.result(r==1);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @PostMapping
    @ApiOperation("根据条件查询")
    @SysRequestLog(description = "查询主题",actionType = ActionType.SELECT)
    public Result getTopic(@RequestBody IndexTopicQuery indexTopicQuery){
        Example example = this.pageQuery(indexTopicQuery, IndexTopic.class);
        return this.vList(indexTopicService.findByExample(example));
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @PatchMapping(path = "config")
    @ApiOperation("设置默认主题")
    @SysRequestLog(description = "设置默认主题",actionType = ActionType.UPDATE)
    public Result setDefaultTopic(@RequestBody IndexTopicQuery indexTopicQuery) {
        if (indexTopicQuery.getId() == null) {
            return this.result(false);
        }
        Example example = new Example(IndexTopic.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupDefault", PageConstants.IS_OK);
        List<IndexTopic> topicList = indexTopicService.findByExample(example);
        if (CollectionUtils.isNotEmpty(topicList)) {
            for (IndexTopic indexTopic : topicList) {
                indexTopic.setGroupDefault(PageConstants.IS_NOT);
                indexTopicService.updateSelective(indexTopic);
            }
        }
        IndexTopic indexTopic = new IndexTopic();
        indexTopic.setId(indexTopicQuery.getId());
        indexTopic.setGroupDefault(PageConstants.IS_OK);
        int result = indexTopicService.updateSelective(indexTopic);
        return this.result(result == 1);
    }
}
