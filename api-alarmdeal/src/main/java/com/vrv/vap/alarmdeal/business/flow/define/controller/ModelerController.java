package com.vrv.vap.alarmdeal.business.flow.define.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.alarmdeal.business.flow.define.service.ModelService;
import com.vrv.vap.alarmdeal.business.flow.define.util.ToWeb;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by liuruijie on 2017/4/20.
 * 模型管理
 */
@RestController
@RequestMapping("models")
public class ModelerController implements RestServiceController<Model, String> {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ModelService modelService;

    /**
     * 新建一个空模型
     * @return
     * @throws IOException 
     */
    @GetMapping("newModel")
    public Result<String> newModel(HttpServletResponse response) {
        //设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String processCode = "process";
        String id = modelService.newModel(name, description, revision, processCode);
        Result<String> result = ResultUtil.success(id);
        return result;
    }


    /**
     * 发布模型为流程定义
     * @param id
     * @return
     * @throws Exception
     */
    @PostMapping("{id}/deployment")
    public Result<DeploymentResponse> deploy(@PathVariable("id")String id) throws Exception {
    	
        //获取模型
        DeploymentResponse deployModel = modelService.deployModel(id);

        return ResultUtil.success(deployModel);
    }
    
    
  

    @Override
    public Object getOne(@PathVariable("id") String id) {
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        return ToWeb.buildResult().setObjData(model);
    }

    @Override
    public Object getList(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
        List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1)
                , rowSize);
        long count = repositoryService.createModelQuery().count();

        return ToWeb.buildResult().setRows(
                ToWeb.Rows.buildRows().setCurrent(page)
                        .setTotalPages((int) (count/rowSize+1))
                        .setTotalRows(count)
                        .setList(list)
                        .setRowSize(rowSize)
        );
    }

    @Override
	public Object deleteOne(@PathVariable("id")String id){
        repositoryService.deleteModel(id);
        return ToWeb.buildResult().refresh();
    }

    @Override
    public Object postOne(@RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object putOne(@PathVariable("id") String s, @RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object patchOne(@PathVariable("id") String s, @RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }
}
