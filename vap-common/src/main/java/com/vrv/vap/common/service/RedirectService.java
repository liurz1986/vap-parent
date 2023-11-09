package com.vrv.vap.common.service;

import com.vrv.vap.common.model.Product;
import com.vrv.vap.common.model.RedirectModel;

import java.util.List;
import java.util.Map;

public interface RedirectService {
    Product getProduct();

    void saveProduct(Product product);

    void saveRedirect(String serviceId, List<RedirectModel> redirectModels);

    List<RedirectModel> getRedirect(String serviceId);

    void initProduct(String scanPath,String productId);

    void initRedirect(String scanPath);
}
