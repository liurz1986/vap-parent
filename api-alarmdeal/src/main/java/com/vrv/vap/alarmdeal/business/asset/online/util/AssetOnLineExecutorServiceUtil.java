package com.vrv.vap.alarmdeal.business.asset.online.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AssetOnLineExecutorServiceUtil {
    private static ExecutorService assetOnLinePool = new ThreadPoolExecutor(20, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(40));

    public static ExecutorService getAssetOnLinePool(){
        return assetOnLinePool;
    }
}
