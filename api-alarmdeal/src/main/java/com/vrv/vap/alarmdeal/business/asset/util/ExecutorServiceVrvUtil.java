package com.vrv.vap.alarmdeal.business.asset.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池 20210730
 */
public class ExecutorServiceVrvUtil {
    private static ExecutorService asynSendAndSubmitThreadPool = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(40));

    public static ExecutorService getThreadPool(){
       return  asynSendAndSubmitThreadPool;
    }
}
