package com.vrv.vap.data.service.feign;

import com.vrv.vap.common.vo.VData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author lilang
 * @date 2019/5/24
 * @description 用户登录
 */
@Component
@FeignClient(value = "api-admin")
public interface AdminClient {

    @RequestMapping(method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE,value = "/file/upload")
    public VData uploadFile(@RequestPart("file") MultipartFile file,
                            @RequestParam("namespace") String namespace, @RequestParam(value = "msg", required = false) String msg, @RequestHeader(value = "Cookie") String Cookie);
}
