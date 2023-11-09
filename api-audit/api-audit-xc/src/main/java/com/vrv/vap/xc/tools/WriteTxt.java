package com.vrv.vap.xc.tools;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.List;

public class WriteTxt {
    private static Log log = LogFactory.getLog(WriteTxt.class);

    public static File getFile(String txtName, String path) {
        //获取根路径
//        File filePath = null;
//        try {
//            filePath = ResourceUtils.getFile("classpath:");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //创建threat文件夹
        File file1 = new File(path + File.separator + "threatout");
        if (!file1.exists()) {
            file1.mkdir();
        }
        File file2 = new File(path + File.separator + "threat");
        if (!file2.exists()) {
            file2.mkdir();
        }
        //创建txt文件
        String filenameTemp = path + File.separator + "threat" + File.separator + txtName + ".txt";
        File file = new File(filenameTemp);
        if (file.exists()) {
            log.info("文件:" + file.getName() + "，已经存在");
            if (file.delete()) {
                log.info("删除成功");
            }
        }
        try {
            if (file.createNewFile()) {
                log.info("文件:" + file.getName() + "，创建成功");
            }
        } catch (IOException e) {
            log.error("", e);
        }
        return file;
    }

//    public static void writeTxt(String txtName, List<?> list, String path) throws IOException {
//        File file = getFile(txtName, path);
//        //写入数据
//        FileWriter fw = null;
//        BufferedWriter bw = null;
//        try {
//            fw = new FileWriter(file, true);
//            bw = new BufferedWriter(fw);
//            String c = "";
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i) instanceof BaseThreatFeatures || list.get(i) instanceof BaseThreatInfo) {
//                    String b = list.get(i).toString() + "\r\n";
//                    c = c + b;
//                }
////                else if (list.get(i) instanceof BaseThreatInfoQuery) {
////                    String b = list.get(i).toString() + "\r\n";
////                    c = c + b;
////                }
//            }
//            bw.write(c);
//            bw.flush();
//        } catch (Exception e) {
//            log.error("", e);
//        } finally {
//            if (bw != null) {
//                bw.close();
//            }
//            if (fw != null) {
//                fw.close();
//            }
//        }
//    }

    public static void writeTxt1(String txtName, String str, String path) throws IOException {
        File file = getFile(txtName, path);
        //写入数据
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(str);
            bw.flush();
        } catch (IOException e) {
            log.error("", e);
        } finally {
            if (bw != null) {
                bw.close();
            }
            if (fw != null) {
                fw.close();
            }
        }
    }

    public static void writeFile(File file, List<String> list) {
        //写入数据
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(String.join(System.lineSeparator(), list));
            bw.flush();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    public static void writeFile2(File file, InputStream in) {
        //写入数据
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            IOUtils.copy(in, fw);
            fw.flush();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(fw);
        }
    }

    public static void main(String[] args) throws Exception {
//        List<String> list = new ArrayList<>();
//        list.add("55");
//        list.add("66666");
//        writeFile(new File("D://abc.txt"), list);
    }
}
