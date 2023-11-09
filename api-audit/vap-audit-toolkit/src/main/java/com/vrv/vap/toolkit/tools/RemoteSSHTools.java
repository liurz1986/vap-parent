package com.vrv.vap.toolkit.tools;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.vrv.vap.toolkit.tools.jsch.FileProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * ssh工具类
 */
public class RemoteSSHTools implements Closeable {

    private static final Log log = LogFactory.getLog(RemoteSSHTools.class);

    private Session session;

    private boolean isLocal;

    public Session getSession() {
        return session;
    }

    public boolean isLocal() {
        return isLocal;
    }

    private RemoteSSHTools() {
    }

    /**
     * 构造连接工具(开启session时会根据host区分本机还是远程模式)
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @return
     */
    public static RemoteSSHTools build(String host, int port, String user, String password) throws JSchException {
        RemoteSSHTools remoteSSHTools = new RemoteSSHTools();
        String localhost = "localhost";
        String localhost127 = "127.0.0.1";
        //判断是否本地执行模式
        if (localhost.equals(host) || localhost127.equals(host)) {
            remoteSSHTools.isLocal = true;
        } else {
            remoteSSHTools.session = openRemoteSession(host, port, user, password);
        }
        return remoteSSHTools;
    }

    public static Session openRemoteSession(String host, int port, String user, String password) throws JSchException {
        return openRemoteSession(host, port, user, password, false);
    }

    public static Session openRemoteSession(String host, int port, String user, String password, boolean withoutLogin) throws JSchException {
        Session session = null;
        try {
            // 建立远程连接
            JSch jsch = new JSch();
            if (log.isDebugEnabled()) {
                log.debug("ssh > " + host + ":" + port);
            }
            session = jsch.getSession(user, host, port);
            session.setConfig("PreferredAuthentications", "password,publickey,gssapi-with-mic,keyboard-interactive");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(30 * 1000);
            if (withoutLogin) {
                //设置免密(开启则需注释掉密码)
                String pubKeyPath = "/root/.ssh/id_rsa";
                jsch.addIdentity(pubKeyPath);
            }
            if (!withoutLogin) {
                session.setPassword(password);
            }
            session.connect();

        } catch (JSchException e) {

            log.error("", e);
            if (session != null) {
                session.disconnect();
            }
            throw e;
        } finally {

        }
        return session;
    }

    /**
     * 执行cmd
     *
     * @param command
     * @return
     */
    public String execute(String command) {
        if (isLocal) {
            return localExecuteCmd(command);
        } else {
            return executeCmd(command);
        }
    }

    /**
     * 执行cmd(不等待结果)
     *
     * @param command
     * @return
     */
    public void executeAside(String command) {
        if (isLocal) {
            localExecuteCmd(command, false);
        } else {
            executeCmd(command, false);
        }
    }

    /**
     * 下载文件(含进度)
     *
     * @param filePath
     * @param monitor
     * @return
     * @throws Exception
     */
    public InputStream downloadSftpFile(String filePath, FileProgressMonitor monitor) throws Exception {
        InputStream inputStream = null;
        if (isLocal) {
            File file = new File(filePath);
            if (file.exists()) {
                monitor.setFileSize(file.length());
                monitor.end();
                inputStream = new FileInputStream(file);
            }
        } else {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS lstat = channel.lstat(filePath);
            if (lstat != null) {
                monitor.setFileSize(lstat.getSize());
                inputStream = channel.get(filePath, monitor);
            }
        }
        return inputStream;
    }

    /**
     * 上传文件(含进度)
     *
     * @param is
     * @param dstDir
     * @param fileName
     * @param monitor
     * @throws Exception
     */
    public void uploadSftpFile(InputStream is, String dstDir, String fileName, FileProgressMonitor monitor) throws Exception {
        if (isLocal) {
            FileUtils.copyInputStreamToFile(is, new File(dstDir + "/" + fileName));
        } else {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            Vector ls = channel.ls(dstDir);
            if (ls == null) {
                channel.mkdir(dstDir);
            }
            channel.put(is, dstDir + "/" + fileName, monitor);
        }
    }

    /**
     * 简单文件上传(无进度)
     *
     * @param fileSrc
     * @param dstDir
     * @param fileName
     * @throws Exception
     */
    public void uploadSftpFile(String fileSrc, String dstDir, String fileName) throws Exception {
        if (isLocal) {
            FileUtils.copyFile(new File(fileSrc), new File(dstDir + "/" + fileName));
        } else {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            Vector ls = channel.ls(dstDir);
            if (ls == null) {
                channel.mkdir(dstDir);
            }
            channel.put(fileSrc, dstDir + "/" + fileName);
        }
    }

    private String executeCmd(String command) {
        return executeCmd(command, true);
    }

    public String executeCmd(String command, boolean needReturnMsg) {
        String res = null;
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.setCommand(command + " 2>&1");
            channelExec.connect();
            String line;
            InputStream inputStream = channelExec.getInputStream();
            InputStream errStream = channelExec.getErrStream();
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder out = new StringBuilder();
            long current = System.currentTimeMillis();
            //以下代码是防止阻塞
            boolean hasReturnMsg = false;
            while (needReturnMsg && !hasReturnMsg) {
                long now = System.currentTimeMillis();
                if (!hasReturnMsg && now - current > 10 * 1000) {
                    //10s无结果返回自动超时
                    break;
                }
                if (inputStream.available() <= 0 && errStream.available() <= 0) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    continue;
                }
                while ((line = stdoutReader.readLine()) != null) {
                    out.append(line).append("\r\n");
                    hasReturnMsg = true;
                }
                if (out.length() > 0) {
                    out.delete(out.lastIndexOf("\r\n"), out.length());
                }
                res = out.toString();
                if (res.isEmpty()) {
                    stdoutReader = new BufferedReader(new InputStreamReader(errStream));
                    out = new StringBuilder();
                    while ((line = stdoutReader.readLine()) != null) {
                        out.append(line).append("\r\n");
                        hasReturnMsg = true;
                    }
                    if (out.length() > 0) {
                        out.delete(out.lastIndexOf("\r\n"), out.length());
                    }
                    res = out.toString();
                }
            }
        } catch (JSchException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
        return res;
    }

    public String executeCmdBlock(String command, int second) {
        String res = null;
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.setCommand(command + " 2>&1");
            channelExec.connect();
            String line;
            InputStream inputStream = channelExec.getInputStream();
            InputStream errStream = channelExec.getErrStream();
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder out = new StringBuilder();
            long current = System.currentTimeMillis();
            //以下代码是防止阻塞
            boolean hasReturnMsg = false;
            while (!hasReturnMsg) {
                long now = System.currentTimeMillis();
                if (!hasReturnMsg && now - current > second * 1000) {
                    //10s无结果返回自动超时
                    break;
                }
                while ((line = stdoutReader.readLine()) != null) {
                    out.append(line).append("\r\n");
                    hasReturnMsg = true;
                }
                if (out.length() > 0) {
                    out.delete(out.lastIndexOf("\r\n"), out.length());
                }
                res = out.toString();
                if (res.isEmpty()) {
                    stdoutReader = new BufferedReader(new InputStreamReader(errStream));
                    out = new StringBuilder();
                    while ((line = stdoutReader.readLine()) != null) {
                        out.append(line).append("\r\n");
                        hasReturnMsg = true;
                    }
                    if (out.length() > 0) {
                        out.delete(out.lastIndexOf("\r\n"), out.length());
                    }
                    res = out.toString();
                }
                if (inputStream.available() == 0 && errStream.available() == 0 && line == null) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    break;
                }
                if (line == null) {
                    break;
                }
            }
        } catch (JSchException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
        return res;
    }

    private String localExecuteCmd(String cmd) {
        return localExecuteCmd(cmd, true);
    }

    public String localExecuteCmd(String cmd, boolean needReturnMsg) {
        log.info("执行本地命令[" + cmd + "]");
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while (needReturnMsg && (line = stdoutReader.readLine()) != null) {
                out.append(line).append("\r\n");
            }

            if (out.length() == 0) {
                stdoutReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                out = new StringBuffer();
                while (needReturnMsg && (line = stdoutReader.readLine()) != null) {
                    out.append(line).append("\r\n");
                }
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
            }
            process.destroy();
            if (out.length() > 0) {
                out.delete(out.lastIndexOf("\r\n"), out.length());
            }
            return out.toString();
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }
}
