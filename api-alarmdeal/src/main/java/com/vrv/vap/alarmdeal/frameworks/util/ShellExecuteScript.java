package com.vrv.vap.alarmdeal.frameworks.util;

import com.vrv.vap.alarmdeal.business.analysis.vo.BooleanMessageVO;
import com.vrv.vap.jpa.common.PathUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * java调用shell脚本工具类;
 *
 * @author wd-pc
 */

public class ShellExecuteScript {

    private static Logger logger = LoggerFactory.getLogger(ShellExecuteScript.class);

    /**
     * java执行shell执行命令行,返回对应的数据
     *
     * @param cmd
     * @return
     */
    public static List<String> queryExecuteCmd(String cmd) {
        List<String> stringList = new ArrayList<>();
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            int exitValue = pro.waitFor();
            if (0 == exitValue) {//等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    stringList.add(line);
                }
                in.close();
                read.close();
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    stringList.add(line);
                }
                errorStream.close();
                read.close();
            }
        } catch (Exception e) {
            logger.error("执行shell脚本异常", e);
            //System.out.println("执行shell脚本异常");
            throw new RuntimeException(e.getMessage());
        }
        return stringList;
    }

    /**
     * java调用shell执行是否返回正确结果
     *
     * @param command
     * @return
     */

    public static String getSafeCommand(String command) {
		// return safe command
        StringBuffer safeCommand = new StringBuffer();
		// safe command white list
        String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-=[]\\',./ ~!@#$%^*()_+\"{}:<>?";
        char[] safeCommandChars = command.toCharArray();
        for (int i = 0, length = safeCommandChars.length; i < length; i++) {
            int whiteListIndex = whiteList.indexOf(safeCommandChars[i]);
            if (-1 == whiteListIndex) {
                return safeCommand.toString();
            }
            safeCommand.append(whiteList.charAt(whiteListIndex));
        }
        return safeCommand.toString();
    }

    public static boolean executeShellByResult(String cmd) {
        try {
			cmd = getSafeCommand(cmd);
			Process pro = Runtime.getRuntime().exec(cmd);
            int exitValue = pro.waitFor();
            if (exitValue == 0) { //等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("log info:" + line);
                }
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("message:" + line);
                }
                errorStream.close();
                read.close();

            }
        } catch (Exception e) {
            logger.error("执行shell脚本异常", e);
            return false;
        }
        return true;
    }

    /**
     * 包括错误信息和对应的结果
     *
     * @param command
     * @return
     */
    public static BooleanMessageVO shellByResultArray(String[] command) {
        BooleanMessageVO booleanMessageVO = new BooleanMessageVO();
        StringBuffer sb = new StringBuffer();
        try {
            logger.info("shell开始执行：");
            Process pro = Runtime.getRuntime().exec(command);
            int exitValue = pro.waitFor();
            logger.info("shell执行结果：" + exitValue);
            if (exitValue == 0) { //等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("log info:" + line);
                }
                booleanMessageVO.setErrorMessage("executing success");
                booleanMessageVO.setResult(true);
                in.close();
                read.close();
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("error message:" + line);
                    sb.append(line);
                    //System.out.println("error message:"+line);
                }
                errorStream.close();
                read.close();

                InputStream input = pro.getInputStream();
                BufferedReader inputread = new BufferedReader(new InputStreamReader(input));
                String inputline = null;
                while ((inputline = inputread.readLine()) != null) {
                    logger.info("message:" + inputline);
                }
                booleanMessageVO.setErrorMessage(sb.toString());
                booleanMessageVO.setResult(false);
                input.close();
                inputread.close();
            }
        } catch (Exception e) {
            logger.error("执行shell脚本异常", e);
            booleanMessageVO.setErrorMessage(e.getMessage());
            booleanMessageVO.setResult(false);
            //logger.error("执行shell脚本异常", e);
            //System.out.println("执行shell脚本异常");
            return booleanMessageVO;
        }
        return booleanMessageVO;

    }

    /**
     * 主要用于带多个参数，且参数当中存在空格得执行
     *
     * @param command
     * @return
     */
    public static boolean executeShellByResultArray(String[] command) {
        Process pro = null;
        try {
            logger.info("shell开始执行脚本为："+ StringUtils.join(command," "));
            pro = Runtime.getRuntime().exec(command);
            int exitValue = pro.waitFor();
            logger.info("shell执行结果：" + exitValue);
            //System.out.println("shell执行结果："+exitValue);
            if (exitValue == 0) { //等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("log info:" + line);
                    //System.out.println("log info:"+line);
                }
                in.close();
                read.close();
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("error message:" + line);
                    //System.out.println("error message:"+line);
                }
                errorStream.close();
                read.close();

                InputStream input = pro.getInputStream();
                BufferedReader inputread = new BufferedReader(new InputStreamReader(input));
                String inputline = null;
                while ((inputline = inputread.readLine()) != null) {
                    logger.info("message:" + inputline);
                }
                input.close();
                inputread.close();
                return false;
            }
        } catch (Exception e) {
            logger.error("执行shell脚本异常", e);
            //logger.error("执行shell脚本异常", e);
            e.printStackTrace();
            //System.out.println("执行shell脚本异常");
            return false;
        } finally {
            try {
                logger.info("准备进入执行销毁");
                if (pro != null) {
                    logger.info("开始执行销毁");
                    pro.destroy();
                    logger.info("执行销毁完成");
                }
            } catch (Exception e) {
                logger.error("关闭chrome异常", e);
            }
        }
        return true;
    }

    /**
     * 给对应的文件赋予权限
     *
     * @param shell_file_dir
     * @param running_shell_file
     * @return
     */
    public static int settingPrivilege(String shell_file_dir, String running_shell_file) {
        int rc = 0;
        File tempFile = new File(PathUtil.combine(shell_file_dir, running_shell_file));
        ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755", tempFile.getPath());
        Process process;
        try {
            process = builder.start();
            rc = process.waitFor();
            if (rc == 0) {
                // logger.info("文件赋予对应的权限");
            }
        } catch (IOException e) {
            logger.error("I/O失败", e);
        } catch (InterruptedException e) {
            logger.error("线程中断异常", e);
            Thread.currentThread().interrupt();
        }
        return rc;
    }

    /**
     * 执行shell脚本(无参数执行)
     *
     * @param shell_file_dir
     * @param running_shell_file
     * @return
     */
    public static boolean executeShellScript(String shell_file_dir, String running_shell_file) {

        ProcessBuilder pb = new ProcessBuilder("./" + running_shell_file);
        pb.directory(new File(shell_file_dir));
        int runningStatus = 0;
        String s = null;
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                // logger.info(s);
                // System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                // logger.info(s);
                // System.out.println(s);
            }
            try {
                runningStatus = p.waitFor();
                if (runningStatus == 0) {
                    // logger.info("脚本调用正常");
                }
            } catch (InterruptedException e) {
                logger.error("线程中断异常", e);
                Thread.currentThread().interrupt();
            }

        } catch (IOException e) {
            logger.error("I/O异常", e);
        }
        if (runningStatus != 0) {
            return false;
        }
        return true;
    }

    /**
     * 执行shell脚本(有参数执行)
     *
     * @param shell_file_dir
     * @param running_shell_file
     * @param params
     * @return
     */
    public static boolean executeShellScript(String shell_file_dir, String running_shell_file, String[] params) {
        ProcessBuilder pb = new ProcessBuilder("./" + running_shell_file, params[0], params[1], params[2], params[3],
                params[4], params[5], params[6]);
        pb.directory(new File(shell_file_dir));
        int runningStatus = 0;
        String s = null;
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                // logger.info(s);
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                // logger.info(s);
                System.out.println(s);
            }
            try {
                runningStatus = p.waitFor();
                if (runningStatus == 0) {
                    return true;
                }
            } catch (InterruptedException e) {
                logger.error("线程中断异常", e);
                Thread.currentThread().interrupt();
            }

        } catch (IOException e) {
            logger.error("I/O异常", e);
        }
        return false;
    }

    public static List<String> executeShellArrayByResult(String[] command) {
        Process pro = null;
        List<String> stringList = new ArrayList<>();
        try {
            logger.info("shell开始执行：");
            pro = Runtime.getRuntime().exec(command);
            int exitValue = pro.waitFor();
            logger.info("shell执行结果：" + exitValue);
            //System.out.println("shell执行结果："+exitValue);
            if (exitValue == 0) { //等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("log info:" + line);
                    stringList.add(line);
                    //System.out.println("log info:"+line);
                }
                in.close();
                read.close();
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    logger.info("error message:" + line);
                    stringList.add(line);
                    //System.out.println("error message:"+line);
                }
                errorStream.close();
                read.close();

                InputStream input = pro.getInputStream();
                BufferedReader inputread = new BufferedReader(new InputStreamReader(input));
                String inputline = null;
                while ((inputline = inputread.readLine()) != null) {
                    logger.info("message:" + inputline);
                }
                input.close();
                inputread.close();
                //   return false;
            }
        } catch (Exception e) {
            logger.error("执行shell脚本异常", e);
            //logger.error("执行shell脚本异常", e);
            e.printStackTrace();
            //System.out.println("执行shell脚本异常");
            //  return false;
        } finally {
            try {
                logger.info("准备进入执行销毁");
                if (pro != null) {
                    logger.info("开始执行销毁");
                    pro.destroy();
                    logger.info("执行销毁完成");
                }
            } catch (Exception e) {
                logger.error("关闭chrome异常", e);
            }
        }
        return stringList;
    }

    public static void main(String[] args) {
        // String[] params = new String[] { "root", "mysql", "localhost", "3306",
        // "jsoc", "alarmdeal alarmdefine",
        // "/usr/local/innodbBackup.sql" };
        // settingPrivilege(SHELL_FILE_DIR, RUNNING_SHELL_FILE);
        // executeShellScript(SHELL_FILE_DIR, RUNNING_SHELL_FILE, params);
        String cmd = "nohup java -jar /usr/local/vap/cloud/modelmanage/942267365b434bf3a6c5200033b6f6ef/模型test_1.0.jar --info  >/usr/local/log  &";
        executeShellByResult(cmd);
        List<String> list = queryExecuteCmd(cmd);
    }

}
