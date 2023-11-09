package com.vrv.vap.xc.fegin;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.Source;
import com.vrv.vap.xc.model.SourceField;
import com.vrv.vap.xc.model.SourceQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient("api-data")
public interface ApiDataClient {

    /**
     * 获取所有基线任务
     *
     * @return
     */
    @PutMapping("/source")
    public VData<Source> addSource(Source source);

    @PutMapping("/source/field")
    public VData<SourceField> addField(SourceField source);

    @GetMapping("/source/{sourceId}")
    public VData<Source> getSourceById(@PathVariable("sourceId") Integer id);

    @GetMapping("/source/field/{sourceId}")
    public VData<List<SourceField>> getFields(@PathVariable("sourceId") Integer id);

    @PostMapping("/source")
    public VList<Source> querySource(SourceQuery query);

    @GetMapping("/source")
    public VData<List<Source>> queryAllSource();
}
