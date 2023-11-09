package com.vrv.vap.toolkit.tools.jsch;

import com.jcraft.jsch.SftpProgressMonitor;

public class FileProgressMonitor implements SftpProgressMonitor {

    private boolean isEnd = false;

    /**
     * 已传输的数据总大小
     */
    private long transfered;

    /**
     * 文件总大小
     */
    private long fileSize;

    public FileProgressMonitor(long fileSize) {
        this.fileSize = fileSize;
    }

    public FileProgressMonitor() {
    }

    public double getProgress() {
        if (fileSize != 0) {
            double d = ((double) transfered * 100) / (double) fileSize;
            return d;
        }
        return 0;
    }


    @Override
    public boolean count(long count) {
        if (isEnd()) {
            return false;
        }
        add(count);
        return true;
    }

    /**
     * ! sftp下载文件时不要主动调用这个方法
     */
    @Override
    public void end() {
        setEnd(true);
    }

    private synchronized void add(long count) {
        transfered = transfered + count;
    }

    private synchronized long getTransfered() {
        return transfered;
    }

    public synchronized void setTransfered(long transfered) {
        this.transfered = transfered;
    }

    private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
    }
}