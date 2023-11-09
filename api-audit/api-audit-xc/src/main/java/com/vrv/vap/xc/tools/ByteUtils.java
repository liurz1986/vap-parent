package com.vrv.vap.xc.tools;

import com.alibaba.druid.sql.visitor.functions.Char;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ByteUtils {

    public static String getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes).flip();
        CharBuffer cb = cs.decode(bb);
        StringBuffer sb =  new StringBuffer();
        for(char c : cb.array()){
            sb.append(c);
        }
        return sb.toString();
    }
}
