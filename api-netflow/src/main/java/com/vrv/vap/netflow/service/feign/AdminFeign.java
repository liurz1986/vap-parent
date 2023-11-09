package com.vrv.vap.netflow.service.feign;


import com.vrv.vap.common.vo.VData;
import com.vrv.vap.netflow.model.CollectorDataAccess;
import com.vrv.vap.netflow.model.CollectorIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "api-admin")
public interface AdminFeign {
    
	@GetMapping(value = "/collector/data/access/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public VData<CollectorDataAccess> getDataAccess(@PathVariable(value = "id") Integer id);

	@GetMapping(value = "/collector/index/{accessId}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public VData<CollectorIndex> getOfflineIndex(@PathVariable(value = "accessId") Integer accessId);
}
