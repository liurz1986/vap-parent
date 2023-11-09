package com.vrv.vap.common.service.impl;

import com.vrv.vap.common.annotation.RedirectMapping;
import com.vrv.vap.common.model.Product;
import com.vrv.vap.common.model.RedirectModel;
import com.vrv.vap.common.service.RedirectService;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

@Service
public class RedirectServiceImpl implements RedirectService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, Product> productRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> productRefreshRedisTemplate;

    @Value("${spring.application.name}")
    private String serviceId;

    private static final String PRODUCT_INFO  ="product:info";

    private static final String PRODUCT_REFRESH_TIME  ="product:refresh_time";

    private static Product localProduct = new Product();

    private static String  localTimemills = String.valueOf(System.currentTimeMillis());



    @Override
    public Product getProduct() {
        String redisTimeMills = productRefreshRedisTemplate.opsForValue().get(PRODUCT_REFRESH_TIME);
        if(redisTimeMills == null)
            return null;
        if(!redisTimeMills.equals(localTimemills)){
            localProduct = productRedisTemplate.opsForValue().get(PRODUCT_INFO);
            localTimemills = redisTimeMills;
        }
        return localProduct;
    }

    @Override
    public void saveProduct(Product product) {
        if(product.getRedirectMap()!=null){
            product.setServices(new HashSet<String>(product.getRedirectMap().keySet()));
        }
        productRedisTemplate.opsForValue().set(PRODUCT_INFO,product);
        productRefreshRedisTemplate.opsForValue().set(PRODUCT_REFRESH_TIME,String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void saveRedirect(String serviceId, List<RedirectModel> redirectModels) {
        if (!productRedisTemplate.hasKey(PRODUCT_INFO))
            return;
        Product product = productRedisTemplate.opsForValue().get(PRODUCT_INFO);
        if (product == null )
            return;
        if( product.getRedirectMap() == null)
            product.setRedirectMap(new HashMap<>());
        if(product.getServices() == null){
            product.setServices( new HashSet<>());
        }
        product.getServices().add(serviceId);
        Map<String,List<RedirectModel>> stringListMap =  product.getRedirectMap();
        stringListMap.put(serviceId,redirectModels);
        productRedisTemplate.opsForValue().set(PRODUCT_INFO,product);
        productRefreshRedisTemplate.opsForValue().set(PRODUCT_REFRESH_TIME,String.valueOf(System.currentTimeMillis()) );
    }

    @Override
    public List<RedirectModel> getRedirect(String serviceId) {
        String redisTimeMills = productRefreshRedisTemplate.opsForValue().get(PRODUCT_REFRESH_TIME);
        if(redisTimeMills == null)
            return null;
        if(!redisTimeMills.equals(localTimemills)){
            localProduct = productRedisTemplate.opsForValue().get(PRODUCT_INFO);
            localTimemills = redisTimeMills;
        }
        Product product = localProduct;
        if (product == null || product.getRedirectMap() == null || !product.getRedirectMap().containsKey(serviceId))
            return null;
        return product.getRedirectMap().get(serviceId);
    }

    @Override
    public void initProduct(String scanPath,String productId) {
        logger.info("===============》开始扫描重定向扫描包地址："+scanPath);
        logger.info("===============》产品ID："+productId);
        logger.info("===============》服务ID："+serviceId);
        Product productInfo = getProduct();
        if (productInfo == null || !productId.equals(productInfo.getId())) {
            productInfo = new Product();
            productInfo.setId(productId);
        }
        if (productInfo.getRedirectMap() == null) {
            productInfo.setRedirectMap(new HashMap<>());
        }
        List<RedirectModel> redirectModelList = scanPackage(scanPath,productId);
        productInfo.getRedirectMap().put(serviceId,redirectModelList);
        this.saveProduct(productInfo);
    }

    @Override
    public void initRedirect(String scanPath) {
        logger.info("===============》开始扫描重定向扫描包地址1："+scanPath);
        Product productInfo = getProduct();
        if (productInfo == null) {
            logger.info("===============》无已知缓存产品信息");
           return;
        }
        logger.info("===============》产品ID："+productInfo.getId());
        logger.info("===============》服务ID："+serviceId);
        List<RedirectModel> redirectModelList = scanPackage(scanPath,productInfo.getId());
        this.saveRedirect(serviceId,redirectModelList);
    }


    private List<RedirectModel> scanPackage(String scanPath,String productId){
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(scanPath)).setScanners(new MethodAnnotationsScanner()));
        //扫描包内带有@RedirectMapping注解的所有方法集合
        Set<Method> methods = reflections.getMethodsAnnotatedWith(RedirectMapping.class);
        List<RedirectModel> redirectModelList = new ArrayList<>();
        if (methods != null) {
            methods.stream().filter(p -> p.getAnnotation(RedirectMapping.class).project().equals(productId) && (p.isAnnotationPresent(GetMapping.class) || p.isAnnotationPresent(PatchMapping.class) || p.isAnnotationPresent(PutMapping.class) || p.isAnnotationPresent(PostMapping.class) || p.isAnnotationPresent(DeleteMapping.class) || p.isAnnotationPresent(RequestMapping.class)))
                    .forEach(p -> {
                        RequestMapping classMapping = p.getDeclaringClass().getAnnotation(RequestMapping.class);
                        GetMapping getMapping = p.getAnnotation(GetMapping.class);
                        PostMapping postMapping = p.getAnnotation(PostMapping.class);
                        PutMapping putMapping = p.getAnnotation(PutMapping.class);
                        DeleteMapping deleteMapping = p.getAnnotation(DeleteMapping.class);
                        PatchMapping patchMapping = p.getAnnotation(PatchMapping.class);
                        RequestMapping requestMapping = p.getAnnotation(RequestMapping.class);
                        RedirectMapping redirectMapping = p.getAnnotation(RedirectMapping.class);
                        String prefixPath = (classMapping.path() != null && classMapping.path().length > 0) ? classMapping.path()[0] : "";
                        String requestURL = redirectMapping.value();
                        String methodPath = null;
                        RequestMethod methodType = RequestMethod.GET;
                        if (getMapping != null) {
                            methodPath = getMapping.value().length > 0 ? getMapping.value()[0] : null;
                            methodType = RequestMethod.GET;
                        }
                        if (methodPath == null && postMapping != null) {
                            methodPath = postMapping.value().length > 0 ? postMapping.value()[0] : null;
                            methodType = RequestMethod.POST;
                        }
                        if (methodPath == null && putMapping != null) {
                            methodPath = putMapping.value().length > 0 ? putMapping.value()[0] : null;
                            methodType = RequestMethod.PUT;
                        }
                        if (methodPath == null && deleteMapping != null) {
                            methodPath = deleteMapping.value().length > 0 ? deleteMapping.value()[0] : null;
                            methodType = RequestMethod.DELETE;
                        }
                        if (methodPath == null && patchMapping != null) {
                            methodPath = patchMapping.value().length > 0 ? patchMapping.value()[0] : null;
                            methodType = RequestMethod.PATCH;
                        }
                        if (methodPath == null && requestMapping != null) {
                            methodPath = requestMapping.value().length > 0 ? requestMapping.value()[0] : null;
                            methodType = requestMapping.method().length > 0 ? requestMapping.method()[0] : RequestMethod.OPTIONS;
                        }
                        methodPath = methodPath==null?"":methodPath;
                        String redirectURL = prefixPath + methodPath;
                        RedirectModel redirectModel = new RedirectModel();
                        redirectModel.setOriginPath(requestURL);
                        redirectModel.setMethodType(methodType.name());
                        redirectModel.setRedirectPath(redirectURL);
                        redirectModel.setServiceId(serviceId);
                        logger.info(requestURL+"-->"+redirectURL+"   "+methodType.name());
                        redirectModelList.add(redirectModel);

                    });

        }
        return redirectModelList;
    }


}
