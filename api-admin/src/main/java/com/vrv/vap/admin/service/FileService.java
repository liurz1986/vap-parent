package com.vrv.vap.admin.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 创建时间 2019年3月25日 上午10:01:04
 * 类说明：FileService
 */
@FeignClient("server-fileupload")
public interface FileService {
	@RequestMapping(value = "/fileup/download/{fileTag}",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> downFile(@PathVariable("fileTag") String fileTag) ;
}
