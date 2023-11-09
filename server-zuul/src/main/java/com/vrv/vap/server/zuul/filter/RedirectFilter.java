package com.vrv.vap.server.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.vrv.vap.common.model.Product;
import com.vrv.vap.common.model.RedirectModel;
import com.vrv.vap.common.service.RedirectService;
import com.vrv.vap.server.zuul.filter.condition.VAPConditional;
import com.vrv.vap.server.zuul.utils.LogForgingUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 等待完成
 * 1. 登录成功后，回退到登陆前的页面
 * 2.
 */

@Component
@Conditional(VAPConditional.class)
public class RedirectFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 需要登录
     */
    @Value("${promission.auth}")
    private String[] auth;

    @Autowired
    private RedirectService redirectService;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();




    @Override
    public Object run() {
        Product product = redirectService.getProduct();
        if(product == null || product.getRedirectMap() == null)
            return null;
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String serviceId = (String)ctx.get(FilterConstants.SERVICE_ID_KEY);
        if(!product.getRedirectMap().containsKey(serviceId))
            return null;
        String method = request.getMethod().toUpperCase();
        String url = request.getRequestURI(); // 列子 [/api-common/user/login/loginWx]
        int index = url.indexOf("/",1);
        if(index>0) {
            String proxyId = url.substring(0, index);
            String path = url.substring(index);
            List<RedirectModel> redirectModelList = product.getRedirectMap().get(serviceId);
            Optional<RedirectModel> optional = redirectModelList.stream().filter(p -> p.getOriginPath().equals(path) && method.equals(p.getMethodType())).findFirst();
            if (optional.isPresent()) {
                String redirectURL = optional.get().getRedirectPath();
                logger.info("原始请求===>实际地址:" + ctx.get(FilterConstants.REQUEST_URI_KEY) + "===>" + redirectURL);
                ctx.put(FilterConstants.REQUEST_URI_KEY, redirectURL);
                return null;
            }
            optional = redirectModelList.stream().filter(p ->method.equals(p.getMethodType()) && antPathMatcher.match(p.getOriginPath(), path)).findFirst();
            if (optional.isPresent()) {
                String redirectURL = "/"+product.getId()+path;
                logger.info("原始请求===>实际地址1:" + ctx.get(FilterConstants.REQUEST_URI_KEY) + "===>" + LogForgingUtil.validLog(redirectURL));
                ctx.put(FilterConstants.REQUEST_URI_KEY, redirectURL);
                return null;
            }
        }

        return null;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        String uri = context.getRequest().getRequestURI();
        if(StringUtils.isNotEmpty(uri) ){
            for (String pos : auth) {
                if (uri.startsWith(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    //filterOrder：过滤的顺序
    @Override
    public int filterOrder() {
        return 1;
    }

    /* (non-Javadoc)filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
                    pre：路由之前
                    routing：路由之时
                    post： 路由之后
                    error：发送错误调用
     */
    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }


    /**
     * 使用正则表达式提取中括号中的内容
     * @param msg
     * @return
     */
    public static List<String> extractMessageByRegular(String msg){
        List<String> list=new ArrayList<String>();
        String regex = "\\{([^}]*)\\}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(msg);
        while(m.find()){
            list.add(m.group());
        }
        return list;
    }

}
