package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.frameworks.contract.ResultModel;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.ResultBody;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.SysLogBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
 

@FeignClient("server-sys")
public interface ServerSystemFegin {
	@RequestMapping(value = "/fileup/download/{fileTag}",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> downFile(@PathVariable("fileTag") String fileTag) ;
	
	
	@RequestMapping(value = "/fileup/uploadFile",method = RequestMethod.POST,consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResultModel uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("namespace") String namespace,
								  @RequestParam("userName") String userName, @RequestParam("userId") String userId, @RequestParam("map") Map<String, Object> map);

	/**
	 * 发送邮件
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/sendEmail/sendSimpleEmail",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultData<Boolean> sendSimpleEmail(@RequestBody Map<String,Object> map);

	/**
	 * 发送syslog
	 */
	@RequestMapping(value="/syslog/addSysLog",method = RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResultBody addSysLog(@RequestBody SysLogBO sysLogBO);
}
