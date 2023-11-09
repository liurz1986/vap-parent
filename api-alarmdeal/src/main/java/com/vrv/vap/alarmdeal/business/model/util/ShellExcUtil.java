package com.vrv.vap.alarmdeal.business.model.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellExcUtil {

    private static Logger logger = LoggerFactory.getLogger(ShellExcUtil.class);

    /**
     * shell脚本执行结果
     * @param cmd
     * @return
     */
    public static void excShellResultThread(String cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                excShellResult(cmd);
            }
        }).start();

    }
    /**
     * shell脚本执行结果
     * @param cmd
     * @return
     */
    public static boolean excShellResult(String cmd) {
        Process process = null;
        boolean result = true;
        try {
            logger.info("shell执行命令："+cmd);
            String[] exc  =new String[]{"sh","-c",cmd};
            process =  Runtime.getRuntime().exec(exc);
            int validate = process.waitFor();
            if(0 == validate){
                logger.info("脚本执行成功");
            }else{
                logger.info("脚本执行失败");
                result = false;
            }
        } catch (IOException e) {
            logger.error("脚本执行异常",e);
            result = false;
        } catch (InterruptedException e) {
            logger.error("脚本执行异常",e);
            result = false;
        }finally {
            if(null != process){
                process.destroy();  //销毁子进程
            }
        }
        return result;
    }

    /**
     * shell脚本执行结果
     * @param cmd
     * @return
     */
    public  static List<String> excShellList(String cmd) {
        List<String> stringList = new ArrayList<>();
        Process pro = null;
        try{
            logger.info("shell执行命令："+cmd);
            String[] exc  =new String[]{"sh","-c",cmd};
             pro = Runtime.getRuntime().exec(exc);//该对象的exec()方法指示Java虚拟机创建一个子进程执行指定的可执行程序，并返回与该子进程对应的Process对象实例。
             int exitValue = pro.waitFor(); //等待子进程完成再往下执行，同时接收执行完毕的返回值
            logger.info("shell执行结果：" + exitValue);
            if (0 == exitValue){
                logger.info("脚本执行ok");
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    stringList.add(line);
                }
                in.close();
                read.close();
            }else{
                logger.info("脚本执行失败");
                InputStream input= pro.getErrorStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                String readLine = "";
                while((readLine = buffer.readLine() )!= null){
                    stringList.add(readLine);
                }
                buffer.close();
                input.close();
            }
        }catch(Exception e){
            logger.error("执行shell脚本异常:{}", e);
            throw new RuntimeException(e.getMessage());
        }finally {
            if(null != pro){
                pro.destroy();  //销毁子进程
            }
        }
        logger.info("返回结果："+ JSON.toJSONString(stringList));
        return stringList;
    }
}
