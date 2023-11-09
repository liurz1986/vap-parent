package com.vrv.vap.monitor.server.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtil {

    private static final int BUFFER_SIZE = 8192;

    /**
     * @param srcDir           压缩文件夹路径
     * @param outDir           压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, String outDir, boolean keepDirStructure) throws Exception {
        OutputStream out = new FileOutputStream(new File(outDir));
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            List<File> sourceFileList = new ArrayList<>();
            File sourceFile = new File(srcDir);
            if(!sourceFile.exists()){
                sourceFile.mkdirs();
            }
            sourceFileList.add(sourceFile);
            compress(sourceFileList, zos, keepDirStructure);
            long end = System.currentTimeMillis();
            log.info("【zip压缩完成】，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("【zip压缩】，出错", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param srcDir           压缩文件夹路径
     * @param outDir           压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String[] srcDir, String outDir, boolean keepDirStructure) throws Exception {
        OutputStream out = new FileOutputStream(new File(outDir));
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            List<File> sourceFileList = new ArrayList<File>();
            for (String dir : srcDir) {
                File sourceFile = new File(dir);
                sourceFileList.add(sourceFile);
            }
            compress(sourceFileList, zos, keepDirStructure);
            long end = System.currentTimeMillis();
            log.info("【zip压缩完成】，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("【zip压缩】，出错", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,
     *                         true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }

    private static void compress(List<File> sourceFileList, ZipOutputStream zos, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        for (File sourceFile : sourceFileList) {
            String name = sourceFile.getName();
            if (sourceFile.isFile()) {
                zos.putNextEntry(new ZipEntry(name));
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            } else {
                File[] listFiles = sourceFile.listFiles();
                if (listFiles == null || listFiles.length == 0) {
                    if (keepDirStructure) {
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        zos.closeEntry();
                    }
                } else {
                    for (File file : listFiles) {
                        if (keepDirStructure) {
                            compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                        } else {
                            compress(file, zos, file.getName(), keepDirStructure);
                        }

                    }
                }
            }
        }
    }


}

