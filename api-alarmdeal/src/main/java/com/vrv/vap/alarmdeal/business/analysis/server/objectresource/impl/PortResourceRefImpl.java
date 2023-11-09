package com.vrv.vap.alarmdeal.business.analysis.server.objectresource.impl;

import com.vrv.vap.alarmdeal.business.analysis.server.objectresource.PortResourceRef;
import org.springframework.stereotype.Service;

@Service
public class PortResourceRefImpl implements PortResourceRef {

    String[] content;




    public void setContent(String[] content){
        this.content=content;
    }


    /**
     *端口资源匹配
     * @param fieldValue
     * @param opt 0代表等于，1代表不等于
     */
    @Override
    public   boolean computer(Object fieldValue,Boolean opt){
        Boolean bool=false;
        int portInt=(int)fieldValue;
        for(String ports:content){
            //端口范围
            if(ports.contains("~")){
                String[] portArray=ports.split("~");
                int startPort=Integer.parseInt(portArray[0]);
                int endPort=Integer.parseInt(portArray[1]);
                if(portInt>=startPort&&portInt<=endPort){
                    bool=!bool;
                    break;
                }
            }else{
                if(portInt==Integer.parseInt(ports)){
                    bool=!bool;
                    break;
                }
            }
        }
        if(!opt){
            bool=!bool;
        }
        return bool;

    }
}
