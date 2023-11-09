package com.vrv.vap.line.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

public class LogUtil {
//    private static String path = "/vrv/log/";
    private static String path = "E:\\tmp\\";
    private static String file = "tt.log";
    private FileWriter writer;
    private BufferedWriter bufferedWriter;
    static {
        File foder = new File(path);
        if(!foder.exists()){
            foder.mkdirs();
        }
        File f = new File(path+file);
        try{
            if(!f.exists()){
                f.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LogUtil() {
        try {
            writer = new FileWriter(path+file,true);
            bufferedWriter = new BufferedWriter(writer,1024);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void info(String info){
        try {
            info = info + "\r\n";
            bufferedWriter.write(info);
            bufferedWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            if(this.bufferedWriter != null){
                bufferedWriter.close();
            }
            if(this.writer != null){
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
