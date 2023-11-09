package com.vrv.vap.server.zuul.fallback;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.netflix.zuul.context.RequestContext;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.server.zuul.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: liujinhui
 * @Date: 2019/8/9 14:18
 * @Desc: 通过API网关来访问其他服务，将路由到此服务停掉，然后再次访问，可以看到回退的内容。  网关熔断
 */
@Component
public class ServiceFallbackProvider implements FallbackProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        logger.error(cause==null?"":cause.getMessage());
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Override
    public ClientHttpResponse fallbackResponse() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR);
    }


//    @Override
    public ClientHttpResponse fallbackResponse(final Throwable cause) {
        logger.error(cause==null?"":cause.getMessage());
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return response(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                //return status;
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                //return status.value();
                return HttpStatus.BAD_REQUEST.value();
            }

            @Override
            public String getStatusText() throws IOException {
                //return status.getReasonPhrase();
                //return HttpStatus.BAD_REQUEST.name();
                return HttpStatus.BAD_REQUEST.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                VData rtn = new VData();
                rtn.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
                rtn.setMessage("后端内部服务异常，无法负载到后端服务！！");
                rtn.setData(String.format("uri:%s", RequestContext.getCurrentContext().getRequest().getRequestURI()));
                logger.error("后端服务异常, " + JsonUtils.toJson(rtn));
                return new ByteArrayInputStream(JsonUtils.toJson(rtn).getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}