package com.vrv.rule.resource.impl;


import com.vrv.rule.resource.IpResourceRef;
import org.springframework.stereotype.Service;

@Service
public class IpResourceRefImpl implements IpResourceRef {


    String[] content;


    public void setContent(String[] content){
        this.content=content;
    }

    /**
     *ip资源匹配
     * @param fieldValue
     * @param opt 0代表等于，1代表不等于
     */
    @Override
    public  boolean computer(Object fieldValue,Boolean opt){
        Boolean bool=false;
        String ip=(String)fieldValue;
        for(String ips:content){
            if(ips.contains("/")){
                if(isInRange(ip,ips)){
                    bool=!bool;
                    break;
                }
            }else{
                if(ip.equals(ips)){
                    bool=!bool;
                    break;
                }
            }
        }
        if(!opt){
            bool=!bool;
        }
        return  bool;
    }

    /**
     * 功能：判断一个IP是不是在一个网段下的
     */
    public  boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

}
